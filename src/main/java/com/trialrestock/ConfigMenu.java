package com.trialrestock;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "trialrestock")
@Config(name = "trialrestock-config", wrapperName = "TrialRestockConfig")
public class ConfigMenu {

	public int costIncrease = 1;
	public long restockDelay = 24000;

}