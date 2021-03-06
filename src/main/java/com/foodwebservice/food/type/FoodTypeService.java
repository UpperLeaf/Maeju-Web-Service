package com.foodwebservice.food.type;

import com.foodwebservice.Ingredient.IngredientType;
import com.foodwebservice.account.Account;
import com.foodwebservice.account.AccountService;
import com.foodwebservice.food.Food;
import com.foodwebservice.food.condition.Kind;
import com.foodwebservice.food.condition.Situation;
import com.foodwebservice.food.condition.Way;
import com.foodwebservice.preference.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FoodTypeService {

    private final PreferenceService preferenceService;
    private final AccountService accountService;
    private List<FoodTypeQuestion> questions;
    

    public List<String> getFoodNames() {
        return questions.stream().map(FoodTypeQuestion::getFoodName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void setPreferencePointBySurvey(Account account, List<String> selected) {
        account = accountService.findById(account.getId());
        preferenceService.setPreferenceCount(account, questions, selected);
    }

    @PostConstruct
    public void initFoodTypeData() throws IOException {
        questions = getFoodString().stream().map((str) -> {
            FoodTypeQuestion foodTypeQuestion = new FoodTypeQuestion();
            String[] values = str.split(",");
            foodTypeQuestion.setFoodName(values[1]);
            for(String kinds : values[2].split("%")){
                if(!kinds.equals("NONE"))
                    foodTypeQuestion.getKinds().add(Kind.getInstanceAsString(kinds));
            }

            for(String ways : values[3].split("%")){
                if(!ways.equals("NONE"))
                    foodTypeQuestion.getWays().add(Way.getInstanceAsString(ways));
            }

            for(String situations : values[4].split("%")) {
                if(!situations.equals("NONE"))
                    foodTypeQuestion.getSituations().add(Situation.getInstanceAsString(situations));
            }

            for(String ingredientTypes : values[5].split("%")) {
                foodTypeQuestion.getIngredients().add(IngredientType.getInstanceAsString(ingredientTypes));
            }
            return foodTypeQuestion;
        }).collect(Collectors.toList());
    }

    private List<String> getFoodString() throws IOException {
        ClassPathResource resource = new ClassPathResource("text/food-type.txt");
        String data = new String(resource.getInputStream().readAllBytes());
        return List.of(data.split("\n"));
    }
}
