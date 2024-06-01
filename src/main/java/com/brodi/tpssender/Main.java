package com.brodi.tpssender;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private JDA jda;

    @Override
    public void onEnable() {
        int time = getConfig().getInt("time");
        Bukkit.getScheduler().runTaskTimer(this, () -> {
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

            getConfig().options().copyDefaults();
            saveDefaultConfig();
            String chanid = getConfig().getString("chanid");
            jda.getTextChannelById(chanid).sendMessage("TPS: " + tpsLast10Secs + ", " + tpsLast5Mins + "\nCPU Usage: " + usagelastMin + msptstring).queue();
        }, 0, time);

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        String token = getConfig().getString("token");
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.watching("your server stats"));
        jda = builder.build();
        System.out.println("successfully started");









    }

}
