/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pdmanager.viewmodels;

import android.content.Context;

import com.wizard.model.AbstractWizardModel;
import com.wizard.model.InfoPage;
import com.wizard.model.PageList;
import com.wizard.model.SingleFixedChoicePage;
import com.wizard.model.SingleFixedChoicePageInverse;

public class BS11WizardModel extends AbstractWizardModel {


    public BS11WizardModel(Context context) {
        super(context);
    }


    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new InfoPage(this, "")
                        .setRequired(true),
                new SingleFixedChoicePageInverse(this, "1. I plan tasks carefully.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(1)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "2. I do things without thinking.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(2)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "3. I make up my mind quickly.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(3)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "4. I am happy-go-lucky.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(4)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "5. I don't \"pay attention\".")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(5)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "6. I have \"racing\" thoughts.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(6)
                        .setRequired(true),
                new SingleFixedChoicePageInverse(this, "7. I plan trips well ahead of time.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(7)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "8. I am self controlled.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(8)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "9. I concentrate easily.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(9)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "10. I save regularly.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(10)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "11. I \"squirm\" at plays or lectures.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(11)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "12. I am a careful thinker.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(12)
                        .setRequired(true),
                new SingleFixedChoicePageInverse(this, "13. I plan for job security.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(13)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "14. I say things without thinking.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(14)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "15. I like to think about complex problems.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(15)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "16. I change jobs.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(16)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "17. I act \"on impulse\".")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(17)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "18. I get easily bored when solving thought problems.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(18)
                        .setRequired(true),
                new SingleFixedChoicePage(this, "19. I act on the spur of the moment.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(19)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "20. I am a steady thinker.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(20)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "21. I change residences.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(21)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "22. I buy things on impulse.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(22)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "23. I can only think about one thing at a time.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(23)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "24. I change hobbies.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(24)
                        .setRequired(true),
                new SingleFixedChoicePage(this, "25. I spend or charge more than I earn.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(25)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "26. I often have extraneous thoughts when thinking.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(26)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "27. I am more interested in the present than the future.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(27)
                        .setRequired(true),

                new SingleFixedChoicePage(this, "28. I am restless at the theater or lectures.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(28)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "29. I like puzzles.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(29)
                        .setRequired(true),

                new SingleFixedChoicePageInverse(this, "30. I am future oriented.")
                        .setChoices("Rarely/Never", "Occasionally", "Often", "Almost Always/Always").setNumberQuestion(30)
                        .setRequired(true)


        );
    }
}
