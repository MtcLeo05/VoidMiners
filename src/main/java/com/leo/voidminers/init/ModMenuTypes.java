package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.menu.SolarControllerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoidMiners.MODID);

    public static final RegistryObject<MenuType<SolarControllerMenu>> SOLAR_CONTROLLER_MENU = MENU_TYPES.register("solar_controller",
        () -> IForgeMenuType.create((windowId, inv, buf) -> new SolarControllerMenu(windowId, inv, buf))
    );
}
