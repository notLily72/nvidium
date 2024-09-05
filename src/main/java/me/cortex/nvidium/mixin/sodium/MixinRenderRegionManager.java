package me.cortex.nvidium.mixin.sodium;

import me.cortex.nvidium.Nvidium;
import me.cortex.nvidium.NvidiumWorldRenderer;
import me.cortex.nvidium.sodiumCompat.INvidiumWorldRendererSetter;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(value = RenderRegionManager.class, remap = false)
public abstract class MixinRenderRegionManager implements INvidiumWorldRendererSetter {


    @Shadow protected abstract void uploadMeshes(CommandList commandList, RenderRegion region, Collection<ChunkBuildOutput> results);

    @Unique private NvidiumWorldRenderer renderer;


    @Redirect(method = "uploadMeshes(Lnet/caffeinemc/mods/sodium/client/gl/device/CommandList;Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegionManager;uploadMeshes(Lnet/caffeinemc/mods/sodium/client/gl/device/CommandList;Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;Ljava/util/Collection;)V"))
    private void redirectUpload(RenderRegionManager instance, CommandList cmdList, RenderRegion pass, Collection<ChunkBuildOutput> uploadQueue) {
        if (Nvidium.IS_ENABLED) {
            uploadQueue.forEach(renderer::uploadBuildResult);
        } else {
            uploadMeshes(cmdList, pass, uploadQueue);
        }
    }

    @Override
    public void setWorldRenderer(NvidiumWorldRenderer renderer) {
        this.renderer = renderer;
    }
}
