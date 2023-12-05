package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.features.module.misc.Debug;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ChatUtils;
import org.lwjgl.input.Mouse;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTool extends Module {

    BooleanSetting switchBackSetting;
    private int lastSlot = -1;

    public AutoTool() {
        super("Auto Tool",0, Category.PLAYER);
    }
    public void init() {
        switchBackSetting = new BooleanSetting("Switch Back ", true);
        addSetting(switchBackSetting);
        super.init();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            if (mc.currentScreen == null && mc.objectMouseOver != null && Mouse.isButtonDown(0)) {
                if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) return;
                IBlockState block = mc.world.getBlockState(mc.objectMouseOver.getBlockPos());
                float strength = 1;
                int bestToolSlot = -1;
                for (int i = 36; i < 45; i++) {
                    ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (itemStack.getDestroySpeed(block) > strength) {
                        strength = itemStack.getDestroySpeed(block);
                        bestToolSlot = i - 36;
                    }
                }

                if (bestToolSlot != -1) {
                    if (bestToolSlot != lastSlot) {
                        lastSlot = mc.player.inventory.currentItem;
                    }

                    mc.player.inventory.currentItem = bestToolSlot;
                    mc.playerController.updateController();
                    if (ModuleManager.getModulebyClass(Debug.class).isEnable()) {
                        ChatUtils.printChat("Switched to " + mc.player.inventory.getCurrentItem().getDisplayName());
                    }
                }
            } else if (switchBackSetting.enable && lastSlot != -1) {
                mc.player.inventory.currentItem = lastSlot;
                lastSlot = -1;
            }
        }
    }
}
