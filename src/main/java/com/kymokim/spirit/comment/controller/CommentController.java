package com.kymokim.spirit.comment.controller;

import com.kymokim.spirit.comment.dto.RequestComment;
import com.kymokim.spirit.comment.dto.ResponseComment;
import com.kymokim.spirit.comment.service.CommentService;
import com.kymokim.spirit.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDto> createComment(@Valid @RequestBody RequestComment.CreateCommentDto createCommentDto
    ) {
        commentService.createComment(createCommentDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(value = "/get-by/post/{postId}")
    public ResponseEntity<ResponseDto> getRootComments(@PathVariable Long postId, @PageableDefault(size = 20) Pageable pageable) {
        Page<ResponseComment.GetRootCommentsDto> dtoPage = commentService.getRootComments(postId, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Root comments retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(value = "/get-by/root-comment/{rootCommentId}")
    public ResponseEntity<ResponseDto> getReplyComments(@PathVariable Long rootCommentId, @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseComment.GetReplyCommentsDto> dtoPage = commentService.getReplyComments(rootCommentId, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reply comments retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<ResponseDto> updateComment(@PathVariable Long commentId, @Valid @RequestBody RequestComment.UpdateCommentDto updateCommentDto) {
        commentService.updateComment(commentId, updateCommentDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
