package com.trialrestock;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;

import java.util.UUID;

public interface TrialVaultServerDataAccess {

    public Object2IntMap<UUID> trialrestock$getPlayerCosts();
    public Object2LongArrayMap<UUID> trialrestock$getPlayerCooldowns();
    public void trialrestock$setPlayerCooldowns(Object2LongArrayMap<UUID> value);

}
