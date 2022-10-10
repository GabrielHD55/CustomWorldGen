package com.gabrielhd.worldgen.biome.types;

import lombok.Getter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Getter
public enum VeinType {
    COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
    IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

    final BlockState ore;
    final BlockState rawOreBlock;
    final BlockState filler;
    private final int minY;
    private final int maxY;

    VeinType(BlockState var2, BlockState var3, BlockState var4, int var5, int var6) {
        this.ore = var2;
        this.rawOreBlock = var3;
        this.filler = var4;
        this.minY = var5;
        this.maxY = var6;
    }
}
