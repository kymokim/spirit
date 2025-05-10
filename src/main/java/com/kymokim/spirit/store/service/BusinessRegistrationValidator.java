package com.kymokim.spirit.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessRegistrationValidator {
    /**
     * 사업자 등록 정보 유효 검증 로직
     * 이 부분은 추후 구현 예정이니 지금은 그냥 메소드 가져다 쓰면 됨(나중에도 input, output은 동일)
     *
     * @param businessRegistrationNumber
     * @param representativeName
     * @param openingDate
     * @return Boolean
     */
    @Transactional
    public Boolean validateBusiness(String businessRegistrationNumber, String representativeName, String openingDate){
        return true;
    }
}
