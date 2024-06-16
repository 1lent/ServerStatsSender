package com.brodi.tpssender;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        super.onSlashCommandInteraction(event);
        Spark spark = SparkProvider.get();

        DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
        double tpsLast10Secs = tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10);
        double tpsLast5Mins = tps.poll(StatisticWindow.TicksPerSecond.MINUTES_5);
        DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();
        double usagelastMin = cpuUsage.poll(StatisticWindow.CpuUsage.MINUTES_1);

        GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> mspt = spark.mspt();
        String msptstring = "";
        if (mspt != null) {
            DoubleAverageInfo msptLastMin = mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1);
            double msptMean = msptLastMin.mean();
            double mspt95Percentile = msptLastMin.percentile95th();
            msptstring = "\nMsptMean Usage: " + msptMean + "\nmspt95Percentile: " + mspt95Percentile;



        }


        if (event.getName().equals("tps")) {

            event.reply("TPS: " + tpsLast10Secs + ", " + tpsLast5Mins + "\nCPU Usage: " + usagelastMin + msptstring).queue();

        }
    }


    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();

        guild.updateCommands()
                .addCommands(Commands.slash("tps", "Shows the server TPS."))
                .queue();
    }

}



