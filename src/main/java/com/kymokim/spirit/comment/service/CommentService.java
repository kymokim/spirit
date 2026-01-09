package com.kymokim.spirit.comment.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.comment.dto.RequestComment;
import com.kymokim.spirit.comment.dto.ResponseComment;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.comment.entity.CommentLike;
import com.kymokim.spirit.comment.exception.CommentErrorCode;
import com.kymokim.spirit.comment.repository.CommentLikeRepository;
import com.kymokim.spirit.comment.repository.CommentRepository;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.notification.dto.comment.RootCommentCreatedNotificationEvent;
import com.kymokim.spirit.post.entity.Post;
import com.kymokim.spirit.post.exception.PostErrorCode;
import com.kymokim.spirit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@MainTransactional
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private Comment resolveComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private boolean isCommentLiked(Long commentId, Long userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    private void validateCommentWriterAccess(Comment comment, Auth user) {
        if (!Objects.equals(comment.getHistoryInfo().getCreatorId(), user.getId()) && !user.getRoles().contains(Role.ADMIN)) {
            throw new CustomException(CommentErrorCode.NOT_COMMENT_WRITER);
        }
    }

    // todo 대댓글 알림 관련 작업 필요
    public ResponseComment.CreateCommentRsDto createComment(RequestComment.CreateCommentRqDto createCommentRqDto) {
        Post post = postRepository.findById(createCommentRqDto.getPostId())
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

        Auth user = AuthResolver.resolveUser();

        Comment comment;
        if (createCommentRqDto.getRootCommentId() == null) {
            comment = createCommentRqDto.toEntity(post, user.getId());
            commentRepository.save(comment);
            if (!comment.isDeleted() && !Objects.equals(comment.getHistoryInfo().getCreatorId(), user.getId())) {
                NotificationEvent.raise(new RootCommentCreatedNotificationEvent(AuthResolver.resolveUser(post.getHistoryInfo().getCreatorId()), user.getNickname(), post.getId()));
            }
        } else {
            Comment rootComment = resolveComment(createCommentRqDto.getRootCommentId());
            if (!rootComment.isRoot()) {
                throw new CustomException(CommentErrorCode.NESTED_REPLY_NOT_ALLOWED);
            }

            comment = Comment.createReply(post, rootComment, createCommentRqDto.getContent(), user.getId());
            commentRepository.save(comment);
            rootComment.increaseReplyCount();
        }
        post.increaseCommentCount();
        return ResponseComment.CreateCommentRsDto.toDto(comment);
    }

    public void likeComment(Long commentId) {
        Comment comment = resolveComment(commentId);
        Auth user = AuthResolver.resolveUser();
        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        if (commentLike == null) {
            commentLike = CommentLike.builder()
                    .userId(user.getId())
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
            comment.increaseLikeCount();
        } else {
            commentLikeRepository.delete(commentLike);
            comment.decreaseLikeCount();
        }
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseComment.GetRootCommentsDto> getRootComments(Long postId, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Comment> commentPage = commentRepository.findByPostIdAndRootCommentIsNullAndIsDeletedFalse(postId, pageable);
            return commentPage.map(comment -> ResponseComment.GetRootCommentsDto.toDto(
                    comment,
                    AuthResolver.resolveUser(comment.getHistoryInfo().getCreatorId()),
                    userId,
                    isCommentLiked(comment.getId(), userId)
            ));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseComment.GetReplyCommentsDto> getReplyComments(Long rootCommentId, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Comment> commentPage = commentRepository.findByRootCommentIdAndIsDeletedFalse(rootCommentId, pageable);
            return commentPage.map(comment -> ResponseComment.GetReplyCommentsDto.toDto(
                    comment,
                    AuthResolver.resolveUser(comment.getHistoryInfo().getCreatorId()),
                    userId,
                    isCommentLiked(comment.getId(), userId)
            ));
        }, 3);
    }

    public void deleteComment(Long commentId) {
        Comment comment = resolveComment(commentId);
        validateCommentWriterAccess(comment, AuthResolver.resolveUser());
        Post post = comment.getPost();
        if (comment.isRoot()) {
            List<Comment> replyCommentList = commentRepository.findAllByRootCommentIdAndIsDeletedFalse(commentId);
            if (replyCommentList != null) {
                replyCommentList.forEach(replyComment -> {
                    replyComment.delete();
                    post.decreaseCommentCount();
                });
            }
        } else {
            Comment rootComment = resolveComment(comment.getRootComment().getId());
            rootComment.decreaseReplyCount();
        }
        commentLikeRepository.deleteByCommentId(commentId);
        post.decreaseCommentCount();
        comment.delete();
    }
}
