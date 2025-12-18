package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.menu.ControllerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
        DeferredRegister.create(Registries.MENU, VoidMiners.MODID);

   
  
    

    public static final DeferredHolder<MenuType<?>, MenuType<ControllerMenu>> CONTROLLER_MENU = 
        MENU_TYPES.register("controller",
            () -> IMenuTypeExtension.create(ControllerMenu::new)
        );
    
}