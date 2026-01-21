package com.kymokim.spirit.agent.service;

import com.kymokim.spirit.agent.dto.LlmAgentResult;
import com.kymokim.spirit.agent.dto.RequestAgent;
import com.kymokim.spirit.agent.dto.SearchConditions;
import com.kymokim.spirit.agent.dto.AgentMode;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Mood;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultAgent implements LlmAgent {

    @Override
    public LlmAgentResult request(RequestAgent requestAgent) {
        String userMessage = requestAgent.getUserMessage() == null ? "" : requestAgent.getUserMessage().toLowerCase();

        SearchConditions.SearchConditionsBuilder searchConditionsBuilder = SearchConditions.builder();

        // 카테고리 설정
        Set<Category> categories = new HashSet<>();
        if (containsAny(userMessage, "이자카야", "일식")) {
            categories.add(Category.IZAKAYA);
        } else if (containsAny(userMessage, "한식")) {
            categories.add(Category.KOREAN);
        } else if (containsAny(userMessage, "양식")) {
            categories.add(Category.WESTERN);
        } else if (containsAny(userMessage, "중식")) {
            categories.add(Category.CHINESE);
        } else if (containsAny(userMessage, "감성주점", "감주")) {
            categories.add(Category.GAMSEONG);
        } else if (containsAny(userMessage, "고깃집", "구이", "찜")) {
            categories.add(Category.GRILLED_STEW);
        } else if (containsAny(userMessage, "호프", "치킨")) {
            categories.add(Category.CHICKEN_HOF);
        } else if (containsAny(userMessage, "해산물", "횟집")) {
            categories.add(Category.RAW_SEAFOOD);
        } else if (containsAny(userMessage, "포차", "포장마차")) {
            categories.add(Category.POCHA);
        } else if (containsAny(userMessage, "펍", "위스키바", "와인바")) {
            categories.add(Category.PUB_BAR);
        }
        if (!categories.isEmpty()) {
            searchConditionsBuilder.categories(categories);
        }

        // 분위기 설정
        Set<Mood> moods = new HashSet<>();
        if (containsAny(userMessage, "조용", "한적", "차분")) {
            moods.add((Mood.QUIET));
        }
        if (containsAny(userMessage, "신나는", "활기찬")) {
            moods.add((Mood.LIVELY));
        }
        if (containsAny(userMessage, "혼술", "혼자")) {
            moods.add((Mood.SOLO_FRIENDLY));
        }
        if (containsAny(userMessage, "데이트", "커플")) {
            moods.add((Mood.DATE));
        }
        if (containsAny(userMessage, "파티", "행사")) {
            moods.add((Mood.PARTY_EVENT));
        }
        if (containsAny(userMessage, "전통적")) {
            moods.add((Mood.TRADITIONAL));
        }
        if (containsAny(userMessage, "이국적")) {
            moods.add((Mood.EXOTIC));
        }
        if (containsAny(userMessage, "모던", "세련", "깔끔")) {
            moods.add((Mood.MODERN));
        }
        if (containsAny(userMessage, "노포")) {
            moods.add((Mood.OLD_SCHOOL));
        }
        if (containsAny(userMessage, "뷰", "야경", "경치")) {
            moods.add((Mood.VIEW_SPOT));
        }
        if (containsAny(userMessage, "로컬", "단골", "동네", "현지")) {
            moods.add((Mood.LOCAL_FAVORITE));
        }
        if (containsAny(userMessage, "2차", "3차", "가볍게")) {
            moods.add((Mood.SECOND_ROUND));
        }
        if (!moods.isEmpty()) {
            searchConditionsBuilder.moods(moods);
        }

        // 방문 예정 시간 설정
        if (containsAny(userMessage, "지금", "바로", "당장", "곧")) {
            searchConditionsBuilder.arrivalTime(LocalDateTime.now());
        } else if (containsAny(userMessage, "내일")) {
            searchConditionsBuilder.arrivalTime(LocalDateTime.now().plusDays(1));
        }

        // 편의시설 여부 설정
        if (containsAny(userMessage, "스크린", "tv")) {
            searchConditionsBuilder.hasScreen(true);
        }
        if (containsAny(userMessage, "룸", "프라이빗")) {
            searchConditionsBuilder.hasRoom(true);
        }
        if (containsAny(userMessage, "야외", "테라스", "루프탑")) {
            searchConditionsBuilder.hasOutdoor(true);
        }
        if (containsAny(userMessage, "단체", "회식", "모임")) {
            searchConditionsBuilder.isGroupAvailable(true);
        }
        if (containsAny(userMessage, "주차")) {
            searchConditionsBuilder.isParkingAvailable(true);
        }
        if (containsAny(userMessage, "콜키지")) {
            searchConditionsBuilder.isCorkageAvailable(true);
        }

        // 주종 설정
        DrinkType drinkType = null;
        if (containsAny(userMessage, "소주", "참이슬", "진로")) {
            drinkType = DrinkType.SOJU;
        } else if (containsAny(userMessage, "맥주", "생맥", "수제맥주")) {
            drinkType = DrinkType.BEER;
        } else if (containsAny(userMessage, "막걸리", "지평생")) {
            drinkType = DrinkType.MAKGEOLLI;
        } else if (containsAny(userMessage, "와인")) {
            drinkType = DrinkType.WINE;
        } else if (containsAny(userMessage, "칵테일")) {
            drinkType = DrinkType.COCKTAIL;
        } else if (containsAny(userMessage, "사케")) {
            drinkType = DrinkType.SAKE;
        } else if (containsAny(userMessage, "고량주")) {
            drinkType = DrinkType.KAOLIANG;
        } else if (containsAny(userMessage, "위스키")) {
            drinkType = DrinkType.WHISKEY;
        } else if (containsAny(userMessage, "보드카")) {
            drinkType = DrinkType.VODKA;
        } else if (containsAny(userMessage, "전통주")) {
            drinkType = DrinkType.TRADITIONAL;
        } else if (containsAny(userMessage, "데킬라")) {
            drinkType = DrinkType.TEQUILA;
        } else if (containsAny(userMessage, "하이볼")) {
            drinkType = DrinkType.HIGHBALL;
        }
        if (drinkType != null) {
            searchConditionsBuilder.drinkType(drinkType);
        }

        // 주종 가격 정렬 방식 설정(내림차순은 제외)
        if (containsAny(userMessage, "싼", "저렴", "가성비", "합리적")) {
            searchConditionsBuilder.drinkPriceOrder(Sort.Direction.ASC);
            if (drinkType == null) {
                searchConditionsBuilder.drinkType(DrinkType.SOJU);
            }
        }

        return LlmAgentResult.builder()
                .agentMessage("지금 말씀하신 조건에 맞춰 추천 리스트를 준비했습니다.")
                .agentMode(AgentMode.SHOW_RESULT)
                .searchConditions(searchConditionsBuilder.build())
                .build();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
