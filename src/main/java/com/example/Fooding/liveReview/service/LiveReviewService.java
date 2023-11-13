package com.example.Fooding.liveReview.service;

import com.example.Fooding.auth.repository.AuthRepository;
import com.example.Fooding.auth.security.JwtAuthToken;
import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.liveReview.dto.RequestLiveReview;
import com.example.Fooding.liveReview.dto.ResponseLiveReview;
import com.example.Fooding.liveReview.entity.LiveReview;
import com.example.Fooding.liveReview.repository.LiveReviewRepository;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LiveReviewService {
    private final StoreRepository storeRepository;
    private final LiveReviewRepository liveReviewRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public void createLiveReview(RequestLiveReview.CreateLiveReviewDto createLiveReviewDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long makerId = authRepository.findByEmail(email).getId();
        Store store = storeRepository.findById(createLiveReviewDto.getStoreId()).get();
        if(store == null) {
            throw new EntityNotFoundException();
        }

        LiveReview liveReview = RequestLiveReview.CreateLiveReviewDto.toEntity(createLiveReviewDto, store, makerId);
        liveReviewRepository.save(liveReview);
    }

    public List<ResponseLiveReview.GetLiveReviewDto> getLiveReviewByStoreId(Store storeId) {
        List<LiveReview> entityList = liveReviewRepository.findAllByStore(storeId);
        List<ResponseLiveReview.GetLiveReviewDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(liveReview -> dtoList.add(ResponseLiveReview.GetLiveReviewDto.toDto(liveReview)));
        return dtoList;
    }

    public void updateLiveReview(RequestLiveReview.UpdateLiveReviewDto updateLiveReviewDto) {
        LiveReview originalLiveReview = liveReviewRepository.findById(updateLiveReviewDto.getLiveReviewId()).get();
        LiveReview updatedLiveReview = RequestLiveReview.UpdateLiveReviewDto.toEntity(originalLiveReview, updateLiveReviewDto);
        liveReviewRepository.save(updatedLiveReview);
    }

    public void deleteLiveReview(Long liveReviewId) {
        LiveReview liveReview = liveReviewRepository.findById(liveReviewId).get();
        liveReviewRepository.delete(liveReview);
    }
}
