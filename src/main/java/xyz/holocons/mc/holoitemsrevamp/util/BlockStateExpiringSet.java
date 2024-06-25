package xyz.holocons.mc.holoitemsrevamp.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockStateExpiringSet extends ExpiringSet<BlockState, Block> {

    public BlockStateExpiringSet(ExpirationPolicy<BlockState> expirationPolicy) {
        super(BlockState::getBlock, expirationPolicy);
    }
}
