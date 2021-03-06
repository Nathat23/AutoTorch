package uk.antiperson.autotorch;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import uk.antiperson.autotorch.config.PlayerConfig;

import java.io.IOException;

public class TorchPlacer {

    private final Player player;
    private final AutoTorch autoTorch;
    private PlayerConfig playerConfig;
    private boolean enabled;


    public TorchPlacer(AutoTorch autoTorch, Player player) {
        this.autoTorch = autoTorch;
        this.player = player;
    }

    public PlayerConfig getPlayerConfig() {
        if (playerConfig == null) {
            playerConfig = new PlayerConfig(autoTorch);
            try {
                playerConfig.init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return playerConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void placeTorch() {
        if (!isEnabled()) {
            return;
        }
        if (autoTorch.getGlobalConfig().isWorldBlacklisted(getPlayer().getWorld())) {
            return;
        }
        Location loc = getPlayer().getLocation().clone();
        int direction = getDirection();
        for (int i = 0; i < getPlayerConfig().getRadius(); i++) {
            Location torchLoc;
            int add = direction <= 2 ? i * -1 : i;
            if (direction % 2 == 1) {
                torchLoc = loc.clone().add(add, 0, 0);
            } else {
                torchLoc = loc.clone().add(0, 0, add);
            }

            Block supporting = torchLoc.clone().subtract(0, 1, 0).getBlock();
            BlockFace attachWall = null;
            if (getPlayerConfig().isAttachToWalls()) {
                Location proposedTorchLocation = torchLoc.clone().add(0, 1, 0);
                Block adjacent = null;
                for (BlockFace blockFace : BlockFace.values()) {
                    if (!blockFace.isCartesian()) continue;
                    if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) continue;
                    if (blockFace == getPlayer().getFacing().getOppositeFace()) continue;
                    Block relative = proposedTorchLocation.getBlock().getRelative(blockFace);
                    if (!relative.isSolid()) continue;
                    adjacent = relative;
                    attachWall = blockFace;
                }
                if (adjacent != null) {
                    System.out.println(attachWall.toString());
                    supporting = adjacent;
                    torchLoc = proposedTorchLocation;
                }
            }
            // check torch location
            Block torchBlock = torchLoc.getBlock();
            if (torchBlock.getType().isSolid()) {
                return;
            }
            if (!(torchLoc.getY() >= getPlayerConfig().getYMin() && torchLoc.getY() <= getPlayerConfig().getYMax())) {
                continue;
            }
            if (!checkSupportingBlock(supporting)) {
                continue;
            }
            if (torchBlock.getLightLevel() > getPlayerConfig().getMinLightLevel()) {
                continue;
            }
            if (autoTorch.getWorldGuardHandler() != null && !autoTorch.getWorldGuardHandler().canPlaceTorch(getPlayer(), torchBlock)){
                continue;
            }
            if (setTorch(torchBlock)) {
                return;
            }
            if (attachWall != null) {
                BlockFace finalAttachWall = attachWall;
                Bukkit.getScheduler().runTaskLater(autoTorch, () -> {
                    BlockState blockState = torchBlock.getState();
                    if (!(blockState instanceof Directional)) {
                        System.out.println("not directional");
                        return;
                    }
                    Directional directional = (Directional) blockState.getBlockData();
                    directional.setFacing(finalAttachWall);
                    blockState.setBlockData(directional);
                    blockState.update(true);
                }, 1);
            }
        }
    }

    private boolean setTorch(Block suggestTorch){
        if (!removeTorches()) {
            return false;
        }
        suggestTorch.setType(Material.TORCH);
        return true;
    }

    private boolean removeTorches() {
        if (!autoTorch.getGlobalConfig().isTorchesFromInventory()){
            return true;
        }
        if (getPlayer().getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        ItemStack toModify = locateTorches();
        if (toModify == null) {
            return false;
        }
        remove(toModify);
        return true;
    }

    private ItemStack locateTorches() {
        switch (getPlayerConfig().getTorchLocation()) {
            case OFF_HAND:
                ItemStack offHand = getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND);
                if (offHand.getType() == Material.TORCH) {
                    return offHand;
                }
                break;
            case HAND:
                ItemStack hand = getPlayer().getInventory().getItem(EquipmentSlot.HAND);
                if (hand.getType() == Material.TORCH) {
                    return hand;
                }
                break;
            case INVENTORY:
                for (ItemStack item : getPlayer().getInventory().getContents()) {
                    if (item == null) {
                        continue;
                    }
                    if (item.getType() != Material.TORCH) {
                        continue;
                    }
                    return item;
                }
                break;
        }
        return null;
    }

    private void remove(ItemStack item) {
        if (item.getAmount() == 1) {
            getPlayer().getInventory().remove(item);
            return;
        }
        item.setAmount(item.getAmount() - 1);
    }

    private int getDirection() {
        BlockFace blockFace = getPlayer().getFacing();
        switch (blockFace) {
            case WEST:
                return 1;
            case NORTH:
                return 2;
            case EAST:
                return 3;
            case SOUTH:
                return 4;
        }
        return 0;
    }

    public boolean checkSupportingBlock(Block block){
        if (autoTorch.getGlobalConfig().isBlockTypeBlacklisted(block)) {
            return false;
        }
        if (Bukkit.getVersion().contains("1.19")) {
            if (block.getBlockData().isFaceSturdy(BlockFace.UP, BlockSupport.FULL)) {
                return true;
            }
        }
        return block.getType().isSolid();
    }

    public Player getPlayer() {
        return player;
    }
}
