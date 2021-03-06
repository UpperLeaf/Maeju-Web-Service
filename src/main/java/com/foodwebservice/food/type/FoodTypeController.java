package com.foodwebservice.food.type;

import com.foodwebservice.account.Account;
import com.foodwebservice.account.CurrentAccount;
import com.foodwebservice.preference.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@RequiredArgsConstructor
@SessionAttributes("foodTypeForm")
@Controller
public class FoodTypeController {
    private final FoodTypeService foodTypeService;

    @ModelAttribute("foodTypeForm")
    public FoodTypeForm foodTypeForm(@CurrentAccount Account account) {
        FoodTypeForm foodTypeForm = new FoodTypeForm();
        foodTypeForm.setFoodsName(foodTypeService.getFoodNames());
        foodTypeForm.setIndex(0);
        foodTypeForm.setEnd(false);
        foodTypeForm.setSequence(Sequence.SIXTH);
        return foodTypeForm;
    }

    @GetMapping("/food/type")
    public String foodTypeView(@CurrentAccount Account account, FoodTypeForm foodTypeForm, Model model,
                               SessionStatus status) {
        model.addAttribute(account);
        model.addAttribute(foodTypeForm);
        if(foodTypeForm.isEnd()){
            foodTypeService.setPreferencePointBySurvey(account, foodTypeForm.getSelected());
            status.setComplete();
        }
        return "food/type";
    }

    @PostMapping("/food/type")
    public String foodTypeRequest(@CurrentAccount Account account, FoodTypeForm foodTypeForm, Model model, String foodName) {
        int index = foodTypeForm.getIndex();

        if(foodTypeForm.getFoodsName().get(index).equals(foodName))
            foodTypeForm.getFoodsName().remove(index + 1);
        else
            foodTypeForm.getFoodsName().remove(index);

        foodTypeForm.getSelected().add(foodName);
        foodTypeForm.setIndex(index + 1);

        if(foodTypeForm.getFoodsName().size() == foodTypeForm.getIndex()) {
            foodTypeForm.setIndex(0);
            foodTypeForm.setSequence(foodTypeForm.getSequence().getPrevious());
        }

        if(foodTypeForm.getFoodsName().size() == 1)
            foodTypeForm.setEnd(true);

        return "redirect:/food/type";
    }


}
