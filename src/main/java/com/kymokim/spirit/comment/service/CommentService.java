package com.kymokim.spirit.comment.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.comment.dto.RequestComment;
import com.kymokim.spirit.comment.dto.ResponseComment;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.comment.exception.CommentErrorCode;
import com.kymokim.spirit.comment.repository.CommentRepository;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
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

    private Comment resolveComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentWriterAccess(Comment comment, Auth user) {
        if (!Objects.equals(comment.getHistoryInfo().getCreatorId(), user.getId()) && !user.getRoles().contains(Role.ADMIN)) {
            throw new CustomException(CommentErrorCode.NOT_COMMENT_WRITER);
        }
    }

    public void createComment(RequestComment.CreateCommentDto createCommentDto) {
        Post post = postRepository.findById(createCommentDto.getPostId())
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

        Long userId = AuthResolver.resolveUserId();

        if (createCommentDto.getRootCommentId() == null) {
            Comment comment = createCommentDto.toEntity(post, userId);
            commentRepository.save(comment);
        } else {
            Comment rootComment = resolveComment(createCommentDto.getRootCommentId());
            if (!rootComment.isRoot()) {
                throw new CustomException(CommentErrorCode.NESTED_REPLY_NOT_ALLOWED);
            }

            Comment reply = Comment.createReply(post, rootComment, createCommentDto.getContent(), userId);
            commentRepository.save(reply);
            rootComment.increaseReplyCount();
        }
        post.increaseCommentCount();
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseComment.GetRootCommentsDto> getRootComments(Long postId, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Comment> commentPage = commentRepository.findByPostIdAndRootCommentIsNullAndIsDeletedFalseOrderByIdDesc(postId, pageable);
            return commentPage.map(comment -> ResponseComment.GetRootCommentsDto.toDto(
                    comment,
                    AuthResolver.resolveUser(comment.getHistoryInfo().getCreatorId()),
                    AuthResolver.resolveUserId()
            ));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseComment.GetReplyCommentsDto> getReplyComments(Long rootCommentId, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Comment> commentPage = commentRepository.findByRootCommentIdAndIsDeletedFalseOrderByIdDesc(rootCommentId, pageable);
            return commentPage.map(comment -> ResponseComment.GetReplyCommentsDto.toDto(
                    comment,
                    AuthResolver.resolveUser(comment.getHistoryInfo().getCreatorId()),
                    AuthResolver.resolveUserId()
            ));
        }, 3);
    }

    public void updateComment(Long commentId, RequestComment.UpdateCommentDto updateCommentDto) {
        Comment comment = resolveComment(commentId);
        validateCommentWriterAccess(comment, AuthResolver.resolveUser());
        comment.update(updateCommentDto.getContent());
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
        comment.delete();
        post.decreaseCommentCount();
    }
}
