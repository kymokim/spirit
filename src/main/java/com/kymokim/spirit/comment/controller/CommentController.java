package com.kymokim.spirit.comment.controller;

import com.kymokim.spirit.comment.dto.RequestComment;
import com.kymokim.spirit.comment.dto.ResponseComment;
import com.kymokim.spirit.comment.service.CommentService;
import com.kymokim.spirit.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<ResponseDto> createComment(@Valid @RequestBody RequestComment.CreateCommentRqDto createCommentRqDto
    ) {
        ResponseComment.CreateCommentRsDto response = commentService.createComment(createCommentRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment created successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/like/{commentId}")
    public ResponseEntity<ResponseDto> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(commentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment liked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(value = "/get-by/comment/{commentId}")
    public ResponseEntity<ResponseDto> getComment(@PathVariable Long commentId) {
        ResponseComment.GetCommentDto dto = commentService.getComment(commentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment retrieved successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(value = "/get-by/post/{postId}")
    public ResponseEntity<ResponseDto> getRootComments(@PathVariable Long postId,
                                                       @ParameterObject @PageableDefault(
                                                               size = 20,
                                                               sort = "id",
                                                               direction = Sort.Direction.DESC
                                                       ) Pageable pageable) {
        System.out.println("Pageable sort: " + pageable.getSort());
        Page<ResponseComment.GetRootCommentsDto> dtoPage = commentService.getRootComments(postId, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Root comments retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping(value = "/get-by/root-comment/{rootCommentId}")
    public ResponseEntity<ResponseDto> getReplyComments(@PathVariable Long rootCommentId,
                                                        @ParameterObject @PageableDefault(
                                                                size = 10,
                                                                sort = "id",
                                                                direction = Sort.Direction.ASC
                                                        ) Pageable pageable) {
        System.out.println("Pageable sort: " + pageable.getSort());
        Page<ResponseComment.GetReplyCommentsDto> dtoPage = commentService.getReplyComments(rootCommentId, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Reply comments retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping(value = "/delete/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
