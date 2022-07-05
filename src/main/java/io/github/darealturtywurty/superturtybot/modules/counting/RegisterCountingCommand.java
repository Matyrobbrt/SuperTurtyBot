package io.github.darealturtywurty.superturtybot.modules.counting;

import java.util.List;

import org.apache.commons.text.WordUtils;

import io.github.darealturtywurty.superturtybot.core.command.CommandCategory;
import io.github.darealturtywurty.superturtybot.core.command.CoreCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RegisterCountingCommand extends CoreCommand {
    public RegisterCountingCommand() {
        super(new Types(true, false, false, false));
    }

    @Override
    public List<OptionData> createOptions() {
        return List.of(new OptionData(OptionType.CHANNEL, "channel", "The channel to start counting on", true),
            new OptionData(OptionType.STRING, "mode", "The counting mode to use", false).addChoice("normal", "normal")
                .addChoice("reverse", "reverse").addChoice("decimal", "decimal").addChoice("maths", "maths")
                .addChoice("binary", "binary").addChoice("ternary", "ternary").addChoice("quaternary", "quaternary")
                .addChoice("quinary", "quinary").addChoice("senary", "senary").addChoice("septenary", "septenary")
                .addChoice("octal", "octal").addChoice("nonary", "nonary").addChoice("undecimal", "undecimal")
                .addChoice("duodecimal", "duodecimal").addChoice("tridecimal", "tridecimal")
                .addChoice("tetradecimal", "tetradecimal").addChoice("pentadecimal", "pentadecimal")
                .addChoice("hexadecimal", "hexadecimal").addChoice("base32", "base32").addChoice("base48", "base48")
                .addChoice("base64", "base64").addChoice("base96", "base96").addChoice("squares", "squares"),
            new OptionData(OptionType.BOOLEAN, "unregister", "Whether or not to unregister this channel", false));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String getDescription() {
        return "Registers a counting channel";
    }

    @Override
    public String getName() {
        return "register-counting";
    }

    @Override
    public String getRichName() {
        return "Register Counting";
    }

    @Override
    protected void runSlash(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            reply(event, "❌ You must be in a server to use this command!", false, true);
            return;
        }

        final TextChannel channel = event.getOption("channel").getAsTextChannel();
        if (channel == null) {
            reply(event, "❌ You must supply a text channel!", false, true);
            return;
        }

        if (!event.getMember().isOwner()) {
            reply(event, "❌ You do not have permission to change the counting mode.", false, true);
            return;
        }
        
        final boolean unregister = event.getOption("unregister", false, OptionMapping::getAsBoolean);
        if (unregister) {
            CountingManager.INSTANCE.removeCountingChannel(event.getGuild(), channel);
            reply(event, "✅ " + channel.getAsMention() + " has been unregistered as a counting channel.");
            return;
        }
        
        final String strMode = event.getOption("mode", null, OptionMapping::getAsString);
        if (strMode == null) {
            reply(event, "❌ You must supply the counting mode!", false, true);
            return;
        }

        final CountingMode mode = CountingMode.valueOf(strMode.toUpperCase().trim());
        if (mode == null) {
            reply(event, "❌ You must supply a valid counting mode!", false, true);
            return;
        }

        if (CountingManager.INSTANCE.isCountingChannel(event.getGuild(), channel)) {
            reply(event, "❌ This channel is already registered as a counting channel!", false, true);
            return;
        }

        CountingManager.INSTANCE.setCountingChannel(event.getGuild(), channel, mode);
        reply(event, "✅ " + channel.getAsMention() + " has now been registered as a counting channel with mode: "
            + WordUtils.capitalize(mode.name().toLowerCase()));
    }
}
