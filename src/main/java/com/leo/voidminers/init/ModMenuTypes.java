package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoidMiners.MODID);

    /*public static final RegistryObject<MenuType<ControllerMenu>> CONTROLLER_MENU = MENU_TYPES.register("controller",
        () -> IForgeMenuType.create(
            ControllerMenu::new
        )
    );*/
}
