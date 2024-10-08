package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.templete.repair.modals;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ModalIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.repiar.Repair;
import org.springframework.stereotype.Component;

@Component
@Data
public class RepairModal {
    Modal civilModal;
    Modal hydroModal;
    Modal doorModal;
    Modal airCondModal;
    Modal otherModal;
    Modal washAndDryModal;
    Modal vendingModal;
    Modal drinkingModal;
    final
    ModalIdSet modalIdSet;

    @PostConstruct
    void init() {
        civilModal = Modal.create(
                modalIdSet.getRepairCivil(), "土木工程報修"
        ).addComponents(
                ActionRow.of(TextInput.create(modalIdSet.getFirstTextInput(), "修繕區域(location)", TextInputStyle.SHORT)
                        .setPlaceholder("請填寫維修區域。 例如：5101-A 或 1AB區樓廁所")
                        .setRequiredRange(0, 20)
                        .build()),
                ActionRow.of(TextInput.create(modalIdSet.getSecondTextInput(), "修繕物品(item)", TextInputStyle.SHORT)
                        .setPlaceholder("請填寫維修物品。 例如：檯燈 或 3號衛浴")
                        .setRequiredRange(0, 20)
                        .build()),
                ActionRow.of(TextInput.create(modalIdSet.getThirdTextInput(), "損壞情形及狀況(description)", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("請敘述損壞情形及狀況。 例如：檯燈不亮 或 堵塞")
                        .setRequiredRange(0, 50)
                        .build()),
                ActionRow.of(TextInput.create(modalIdSet.getFourthTextInput(), "可配合維修時間-公共區域不須填寫(Available Repair Time)", TextInputStyle.SHORT)
                        .setRequired(false)
                        .setPlaceholder("私人區域若無填寫，則直接進入維修。")
                        .setRequiredRange(0, 20)
                        .build()
                )
        ).build();

        hydroModal = civilModal.createCopy().setId(modalIdSet.getRepairHydro()).setTitle("水電工程報修").build();
        doorModal = civilModal.createCopy().setId(modalIdSet.getRepairDoor()).setTitle("門窗鎖具報修").build();
        airCondModal = civilModal.createCopy().setId(modalIdSet.getRepairAirCond()).setTitle("空調設備報修").build();
        otherModal = civilModal.createCopy().setId(modalIdSet.getRepairOther()).setTitle("其他報修").build();
        washAndDryModal = Modal.create(modalIdSet.getRepairWashAndDry(), "洗、烘衣機報修")
                .addComponents(
                        ActionRow.of(
                                TextInput.create(modalIdSet.getFirstTextInput(), "具體位置(Location and item)", TextInputStyle.SHORT)
                                        .setPlaceholder("請填寫具體位置。 例如：三樓AB區三號洗衣機。").build()
                        ),
                        ActionRow.of(
                                TextInput.create(modalIdSet.getSecondTextInput(), "損壞情形及狀況(description)", TextInputStyle.SHORT)
                                        .setPlaceholder("請填寫損壞情形及狀況。 例如： 無法運作。").build()
                        )
                )
                .build();
        vendingModal = washAndDryModal.createCopy().setId(modalIdSet.getRepairVending()).setTitle("販賣機報修").build();
        drinkingModal = washAndDryModal.createCopy().setId(modalIdSet.getRepairDrinking()).setTitle("飲水機報修").build();
    }

    public Modal getModal(Repair.Type type) {
        switch (type) {
            case CIVIL -> {
                return civilModal;
            }
            case HYDRO -> {
                return hydroModal;
            }
            case DOOR -> {
                return doorModal;
            }
            case AIR_COND -> {
                return airCondModal;
            }
            case WASH_AND_DRY -> {
                return washAndDryModal;
            }
            case VENDING -> {
                return vendingModal;
            }
            case DRINKING -> {
                return drinkingModal;
            }
            default -> {
                return otherModal;
            }
        }
    }
}
