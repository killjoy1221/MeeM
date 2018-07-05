package mnm.plugins.meem;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

@Plugin(
        id = "meem",
        name = "MeeM",
        authors = "killjoy1221",
        description = "Basic memory commands.",
        version = "1.0",
        url = "https://github.com/killjoy1221/MeeM"
)
public class Meem {

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .permission("meem.mem")
                .description(Text.of("Shows memory usage of the server."))
                .executor((src, args) -> {
                    long total = Runtime.getRuntime().totalMemory();
                    long free = Runtime.getRuntime().freeMemory();
                    long max = Runtime.getRuntime().maxMemory();
                    long used = total - free;

                    double perc = (double) free / (double) total;
                    TextColor color;
                    if (perc < .2) {
                        color = TextColors.RED;
                    } else if (perc < .4) {
                        color = TextColors.YELLOW;
                    } else {
                        color = TextColors.GREEN;
                    }

                    String ftotal = formatBytes(total);
                    String fmax = formatBytes(max);
                    String fused = formatBytes(used);
                    String fperc = String.format(" (%.1f%% free) ", perc * 100);

                    src.sendMessage(Text.of(fused, "/", ftotal, color, fperc, TextColors.RESET, fmax, " max"));

                    return CommandResult.success();
                })
                .child(CommandSpec.builder()
                        .permission("meem.gc")
                        .description(Text.of("Runs garbage collection to free up memory. This may cause a lag spike."))
                        .executor((src, args) -> {
                            System.gc();
                            src.sendMessage(Text.of(TextColors.GRAY, "GC has completed."));
                            return CommandResult.success();
                        })
                        .build(), "gc")
                .build(), "mem");
    }

    private static String formatBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "kMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
