package me.indian.ostag.util;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.indian.ostag.OsTag;

public class UpDateUtil {

    private final OsTag plugin = OsTag.getInstance();
    private final PluginLogger logger = plugin.getLogger();
    private final Config config = plugin.getConfig();
    private final String debugPrefix = ColorUtil.replaceColorCode(plugin.publicDebugPrefix + "&8[&dAutoUpdate&8] ");
    private final String pluginsPath = Server.getInstance().getPluginPath();
    private final String latestVersion = GithubUtil.getLatestTag();
    private final String currentVersion = plugin.getDescription().getVersion();
    private final String latestUrl = "https://github.com/OpenPlugins-Minecraft/OsTag/releases/download/" + latestVersion + "/OsTag-" + latestVersion + ".jar";
    private final String latestFileName = "OsTag-" + latestVersion + ".jar";
    private final String currentFileName = "Ostag-" + currentVersion + ".jar";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void autoUpDate() {
        if (config.getBoolean("AutoUpdate")) {
            upDate();
        }
    }

    public void manualUpDate() {
        upDate();
    }

    private void upDate() {
        executorService.execute(() -> {
            if (GithubUtil.getFastTagInfo().contains("false")) {
                File latest = new File(pluginsPath + "/" + latestFileName);
                File current = new File(pluginsPath + "/" + currentFileName);

                if (current.exists() && latest.exists()) {
                    if (!currentVersion.equals(latestVersion)) {
                        logger.info(ColorUtil.replaceColorCode("&cYou have downloaded the latest version but you are not using it"));
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!latest.exists()) {
                    logger.info(ColorUtil.replaceColorCode("&aDownloading latest ostag version..."));
                    if (plugin.debug) {
                        logger.info(ColorUtil.replaceColorCode(debugPrefix + "&b" + latestUrl));
                    }
                    downloadLatestVersion();
                }
            } else {
                if (plugin.debug) {
                    logger.info(ColorUtil.replaceColorCode(debugPrefix + "&aDownloading the latest version is unnecessary or not possible"));
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void downloadLatestVersion() {
        try {
            final long millisActualTime = System.currentTimeMillis();
            URL url = new URL(latestUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            int responseCode = httpConnection.getResponseCode();

            // sprawdzanie kodu odpowiedzi HTTP
            if (responseCode == HttpURLConnection.HTTP_OK) {

                String contentType = httpConnection.getContentType();
                int contentLength = httpConnection.getContentLength();

                if (plugin.debug) {
                    logger.info(ColorUtil.replaceColorCode(debugPrefix + "&aFile download: &b" + latestFileName));
                    logger.info(ColorUtil.replaceColorCode(debugPrefix + "&aContent type: &b" + contentType));
                    logger.info(ColorUtil.replaceColorCode(debugPrefix + "&aContent length: &b" + contentLength));
                }
                // otwieranie strumienia wejściowego z połączenia HTTP
                InputStream inputStream = httpConnection.getInputStream();
                String saveFilePath = pluginsPath + File.separator + latestFileName;

                // otwieranie strumienia wyjściowego do zapisu pliku
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                // zapisywanie danych z wejściowego strumienia do wyjściowego strumienia
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // zwalnianie zasobów
                outputStream.close();
                inputStream.close();

                final double executionTimeInSeconds = (System.currentTimeMillis() - millisActualTime) / 1000.0;
                logger.info(ColorUtil.replaceColorCode("&aDownload completed in &b" + executionTimeInSeconds + " &aseconds"));
            } else {
                logger.warning(ColorUtil.replaceColorCode("&cThe file could not be used. HTTP response code:" + responseCode));
                Thread.currentThread().interrupt();
            }
            httpConnection.disconnect();
        } catch (Exception e) {
            logger.warning(ColorUtil.replaceColorCode("&cCan't download latest ostag version!"));
            Thread.currentThread().interrupt();
        }
    }
}