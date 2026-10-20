package com.example;

import com.example.Items.ModItems;
import com.example.guis.TestGUI;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "template-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.initialize();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("test_command").executes(context -> {
				if (!context.getSource().isPlayer()) {
					context.getSource().sendSuccess(() -> Component.literal("test_command sent outside of a player."),
							false);
					return 1;
				}

				SimpleGui g = new TestGUI(context.getSource().getPlayer());

				g.open();

				context.getSource().sendSuccess(() -> Component.literal("Called /test_command."), false);
				return 1;
			}));
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}