package com.kymokim.spirit.post.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.post.dto.RequestPost;
import com.kymokim.spirit.post.dto.ResponsePost;
import com.kymokim.spirit.post.service.PostService;
import com.kymokim.spirit.store.dto.ResponseStore;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Post API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createPost(@RequestPart(value = "files", required = true) MultipartFile[] files,
                                                  @Valid @RequestPart(value = "createPostDto") RequestPost.CreatePostDto createPostDto) {
        postService.createPost(files, createPostDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<ResponseDto> updatePost(@PathVariable("postId") Long postId,
                                                  @Valid @RequestBody RequestPost.UpdatePostDto updatePostDto) {
        postService.updatePost(postId, updatePostDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ResponseDto> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/save/{postId}")
    public ResponseEntity<ResponseDto> savePost(@PathVariable("postId") Long postId) {
        postService.savePost(postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post saved successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<ResponseDto> likePost(@PathVariable("postId") Long postId) {
        postService.likePost(postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post liked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/share/{postId}")
    public ResponseEntity<ResponseDto> sharePost(@PathVariable("postId") Long postId) {
        ResponsePost.SharePostDto sharePostDto = postService.sharePost(postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post share link created successfully.")
                .data(sharePostDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/upload-image/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadPostImage(@RequestPart(value = "files", required = true) MultipartFile[] files,
                                                       @PathVariable("postId") Long postId) {
        postService.uploadImage(files, postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete-image/{postId}")
    public ResponseEntity<ResponseDto> deletePostImage(@Valid @RequestBody RequestPost.DeleteImageDto deleteImageDto,
                                                       @PathVariable("postId") Long postId) {
        postService.deleteImage(deleteImageDto, postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/{postId}")
    public ResponseEntity<ResponseDto> getPost(@PathVariable("postId") Long postId) {
        ResponsePost.GetPostDto response = postService.getPost(postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/store/{storeId}")
    public ResponseEntity<ResponseDto> getPostByStore(@PathVariable("storeId") Long storeId,
                                                      @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponsePost.GetPostByStoreDto> response = postService.getPostByStore(storeId, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/my")
    public ResponseEntity<ResponseDto> getMyPost(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<ResponsePost.GetMyPostDto> response = postService.getMyPost(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("My post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/recent")
    public ResponseEntity<ResponseDto> getRecentPost(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponsePost.GetRecentPostDto> response = postService.getRecentPost(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Recent post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/saved")
    public ResponseEntity<ResponseDto> getSavedPost(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<ResponsePost.GetSavedPostDto> response = postService.getSavedPost(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Saved post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
