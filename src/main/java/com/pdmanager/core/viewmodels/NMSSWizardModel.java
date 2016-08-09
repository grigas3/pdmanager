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

package com.pdmanager.core.viewmodels;

import android.content.Context;

import com.wizard.model.AbstractWizardModel;
import com.wizard.model.InfoPage;
import com.wizard.model.PageList;
import com.wizard.model.SimpleSpinnerItemChoicePage;

public class NMSSWizardModel extends AbstractWizardModel {


    public NMSSWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                // new BranchPage(this, "First Question")
                //       .addBranch("I plan tasks carefully.",
                new InfoPage(this, "")
                        .setRequired(true),

                new SimpleSpinnerItemChoicePage(this, "1. Does the patient experience light-headedness, dizziness, weakness on standing from sitting or lying position?").setDomain("1").setNumberQuestion(1),

                new SimpleSpinnerItemChoicePage(this, "2. Does the patient fall because of fainting or blacking out?").setDomain("1").setNumberQuestion(2),

                new SimpleSpinnerItemChoicePage(this, "3. Does the patient doze off or fall asleep unintentionally during daytime activities? (For example, during conversation, during mealtimes, or while watching television or reading)").setDomain("2").setNumberQuestion(3),

                new SimpleSpinnerItemChoicePage(this, "4. Does fatigue (tiredness) or lack of energy (not slowness) limit the patient's daytime activities?").setDomain("2").setNumberQuestion(4),

                new SimpleSpinnerItemChoicePage(this, "5. Does the patient have difficulties falling or staying asleep?").setDomain("2").setNumberQuestion(5),

                new SimpleSpinnerItemChoicePage(this, "6. Does the patient experience an urge to move the legs or restlessness in legs that improves with movement when he/she is sitting or lying down inactive?").setDomain("2").setNumberQuestion(6),

                new SimpleSpinnerItemChoicePage(this, "7. Has the patient lost interest in his/her surroundings?").setDomain("3").setNumberQuestion(7),

                new SimpleSpinnerItemChoicePage(this, "8. Has the patient lost interest in doing things or lack motivation to start new activities?").setDomain("3").setNumberQuestion(8),

                new SimpleSpinnerItemChoicePage(this, "9. Does the patient feel nervous, worried or frightened for no apparent reason?").setDomain("3").setNumberQuestion(9),

                new SimpleSpinnerItemChoicePage(this, "10. Does the patient seem sad or depressed or has he/she reported such feelings?").setDomain("3").setNumberQuestion(10),

                new SimpleSpinnerItemChoicePage(this, "11. Does the patient have flat moods without the normal \"highs\" and \"lows\"?").setDomain("3").setNumberQuestion(11),

                new SimpleSpinnerItemChoicePage(this, "12. Does the patient have difficulty in experiencing pleasure from their usual activities or report that they lack pleasure?").setDomain("3").setNumberQuestion(12),

                new SimpleSpinnerItemChoicePage(this, "13. Does the patient indicate that he/she sees things that are not there?").setDomain("4").setNumberQuestion(13),

                new SimpleSpinnerItemChoicePage(this, "14. Does the patient have beliefs that you know are not true? (For example, about being harmed, being robbed or being unfaithful)").setDomain("4").setNumberQuestion(14),

                new SimpleSpinnerItemChoicePage(this, "15. Does the patient experience double vision? (2 seperate real objects and not blurred vision)").setDomain("4").setNumberQuestion(15),

                new SimpleSpinnerItemChoicePage(this, "16. Does the patient have problems sustaining concentration during activities? (For example, reading or having a conversation)").setDomain("5").setNumberQuestion(16),

                new SimpleSpinnerItemChoicePage(this, "17. Does the patient forget things that he/she has been told a short time ago or events that happened in the last few days?").setDomain("5").setNumberQuestion(17),

                new SimpleSpinnerItemChoicePage(this, "18. Does the patient forget to do things? (For example, take tablets or turn off domestic appliances?)").setDomain("5").setNumberQuestion(18),

                new SimpleSpinnerItemChoicePage(this, "19. Does the patient dribble saliva during the day?").setDomain("6").setNumberQuestion(19),

                new SimpleSpinnerItemChoicePage(this, "20. Does the patient having difficulty swallowing?").setDomain("6").setNumberQuestion(20),

                new SimpleSpinnerItemChoicePage(this, "21. Does the patient suffer from constipation? (Bowel action less than three times weekly)").setDomain("6").setNumberQuestion(21),

                new SimpleSpinnerItemChoicePage(this, "22. Doe the patient have difficulty holding urine? (Urgency)").setDomain("7").setNumberQuestion(22),

                new SimpleSpinnerItemChoicePage(this, "23. Does the patient have to void within 2 hours of last voiding? (Frequency)").setDomain("7").setNumberQuestion(23),

                new SimpleSpinnerItemChoicePage(this, "24. Does the patient have to get up regularly at night to pass urine? (Nocturia)").setDomain("7").setNumberQuestion(24),

                new SimpleSpinnerItemChoicePage(this, "25. Does the patient have altered interest in sex? (Very much increased or decreased, please underline)").setDomain("8").setNumberQuestion(25),

                new SimpleSpinnerItemChoicePage(this, "26. Does the patient have problems having sex?").setDomain("8").setNumberQuestion(26),

                new SimpleSpinnerItemChoicePage(this, "27. Does the patient suffer from pain not explained by other known conditions? (Is it related to intake of drugs and is relieved by antiparkinson drugs?)").setDomain("9").setNumberQuestion(27),

                new SimpleSpinnerItemChoicePage(this, "28. Does the patient report a change in ability to taste or smell?").setDomain("9").setNumberQuestion(28),

                new SimpleSpinnerItemChoicePage(this, "29. Does the patient report a recent change in weight? (not related to dieting?)").setDomain("9").setNumberQuestion(29),

                new SimpleSpinnerItemChoicePage(this, "30. Does the patient experience excessive sweating? (not related to hot weather)").setDomain("9").setNumberQuestion(30)


        );
    }
}
