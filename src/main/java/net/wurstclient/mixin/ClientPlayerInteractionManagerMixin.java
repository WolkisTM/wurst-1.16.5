/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.WurstClient;
import net.wurstclient.hack.HackList;
import net.wurstclient.mixinterface.IClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerController.class)
public abstract class ClientPlayerInteractionManagerMixin
        implements IClientPlayerInteractionManager {
    @Shadow
    private Minecraft mc;
    @Shadow
    private float curBlockDamageMP;
    @Shadow
    private boolean isHittingBlock;

    /**
     * blockHitDelay
     */

    @Shadow
    private int blockHitDelay;

    @Inject(at = {@At("HEAD")},
            method = {"getBlockReachDistance()F"},
            cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> ci) {
        HackList hax = WurstClient.INSTANCE.getHax();
        if (hax == null || !hax.reachHack.isEnabled())
            return;

        ci.setReturnValue(hax.reachHack.getReachDistance());
    }

    @Inject(at = {@At("HEAD")},
            method = {"extendedReach()Z"},
            cancellable = true)
    private void hasExtendedReach(CallbackInfoReturnable<Boolean> cir) {
        HackList hax = WurstClient.INSTANCE.getHax();
        if (hax == null || !hax.reachHack.isEnabled())
            return;

        cir.setReturnValue(true);
    }

    @Override
    public float getCurrentBreakingProgress() {
        return curBlockDamageMP;
    }

    @Override
    public void setBlockHitDelay(int delay) {
        blockHitDelay = delay;
    }

    @Override
    public void sendPlayerActionC2SPacket(CPlayerDiggingPacket.Action action, BlockPos blockPos,
                                          Direction direction) {
        sendDiggingPacket(action, blockPos, direction);
    }

    @Shadow
    private void sendDiggingPacket(
            CPlayerDiggingPacket.Action playerActionC2SPacket$Action_1,
            BlockPos blockPos_1, Direction direction_1) {

    }


    @Override
    public ItemStack windowClick_PICKUP(int slot) {
        return windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    @Shadow
    public abstract ItemStack windowClick(int windowId, int slotId, int mouseButton,
                                          ClickType type, PlayerEntity player);
}
