package com.gr8pefish.hardchoices.players;

import com.gr8pefish.hardchoices.HardChoices;
import com.gr8pefish.hardchoices.handlers.ConfigHandler;
import com.gr8pefish.hardchoices.handlers.DisabledHandler;
import com.gr8pefish.hardchoices.networking.NetworkingHelper;
import com.gr8pefish.hardchoices.util.Logger;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

//Just some helper functions for utilizing the player data

public class PlayerData {

    public static ArrayList<ArrayList<String>> modGroups;

    public static void initModGroups(){
        modGroups = initModGroupings();
    }

    public static boolean isModDisabledForPlayer(EntityPlayer player, ItemStack stack){

        ExtendedPlayer playerData = ExtendedPlayer.get(player);
        String modid = getModIdFromItemStack(stack);
        for (String modId : playerData.disabledMods.keySet()){
            if (modId.toLowerCase().trim().equals(modid.toLowerCase().trim())){
//                Logger.log("Disabled mod "+modId+" : "+playerData.disabledMods.get(modId));
                return playerData.disabledMods.get(modId);
            }
        }
        return false;
    }

    public static String getModIdFromItemStack(ItemStack stack){
        GameRegistry.UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        return identifier.modId;
    }

    private static ArrayList<ArrayList<String>> initModGroupings(){
        ArrayList<ArrayList<String>> finalArrayList = new ArrayList<ArrayList<String>>();
        ArrayList<String> allModSets = ConfigHandler.blackList;
        for (String modGroup : allModSets) {
            finalArrayList.add(new ArrayList<String>(Arrays.asList(modGroup.split(","))));
        }
        return finalArrayList;
    }

    public static boolean getBlacklistGroupDisabled(String modid, EntityPlayer player){
        ExtendedPlayer thePlayer = ExtendedPlayer.get(player);
        for (ArrayList<String> set : modGroups) {
            if (set.contains(modid)) {
                for (String modId : set){
                    if (!modId.equals(modid)){
                        return thePlayer.disabledMods.get(modId);
                    }
                }
            }
        }
        return false;
    }

    public static void disableGroupMods(String modid, EntityPlayer player){
        ExtendedPlayer thePlayer = ExtendedPlayer.get(player);
        for (ArrayList<String> set : modGroups) {
            if (set.contains(modid)){
                for (String modId : set) {
                    if (!modId.equals(modid) && !thePlayer.disabledMods.get(modId)) {
                        thePlayer.disabledMods.put(modId, true);
                        if (NetworkingHelper.isClientSide(player)) {
                            Logger.log("Disabled the mod: " + modId); //Just so it only prints once
                        }
                    }
                }
            }
        }
    }

}
