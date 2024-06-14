package com.trialrestock;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Modmenu(modId = "trialrestock")
@Config(name = "trialrestock-config", wrapperName = "TrialRestockConfig")
public class ConfigMenu {

	public int costIncrease = 1;
	public long restockDelay = 24000;

}