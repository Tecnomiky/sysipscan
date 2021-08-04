package net.azib.ipscan;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.CombinedUnprivilegedPinger;
import net.azib.ipscan.core.net.JavaPinger;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.feeders.RangeFeeder;
//import net.azib.ipscan.feeders.RangeFeederFinite;
import net.azib.ipscan.fetchers.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class Main2 {

    public static void main(String... args) {
        System.out.println("start");
        try {

            Preferences prefs = Preferences.userRoot().node("main");
            List<Fetcher> fetchers = new ArrayList<>();
            ScannerConfig scannerConfig = new ScannerConfig(prefs);
            scannerConfig.selectedPinger = "pinger.combined";
            scannerConfig.pingCount = 6;
            PingerRegistry pingerRegistry = new PingerRegistry(scannerConfig);
            fetchers.add(new IPFetcher());
            Pinger javaPinger = new JavaPinger(45);
            Pinger combinedPinger = new CombinedUnprivilegedPinger(45);
            fetchers.add(new PingFetcher(pingerRegistry, scannerConfig, javaPinger));
            fetchers.add(new HostnameFetcher());
            fetchers.add(new PortsFetcher(scannerConfig));
            fetchers.add(new UnixMACFetcher());
            fetchers.add(new MACVendorFetcher(new UnixMACFetcher()));

            FetcherRegistry fetcherRegistry = new FetcherRegistry(fetchers, prefs);
            String[] arrayIDFetchers = {IPFetcher.ID, PingFetcher.ID, HostnameFetcher.ID, PortsFetcher.ID,
                    UnixMACFetcher.ID, MACVendorFetcher.ID};
            fetcherRegistry.updateSelectedFetchers(arrayIDFetchers);
            ScanningResultList scanningResultList = new ScanningResultList(fetcherRegistry);
            Scanner scanner= new Scanner(fetcherRegistry);

            //RangeFeederLoop rangeFeederLoop;
            RangeFeeder rangeFeederFinite;
            ScanningSubject scanningSubject;
            ScanningResult scanningResult;
            Map<InetAddress, ScanningResult> resultList = new HashMap<>();
/*
            InetAddress address = InetAddress.getByName("192.168.1.1");
            RangeFeederFinite rangeFeeder = new RangeFeederFinite("192.168.1.1", "192.168.1.1");
            scanningResultList.initNewScan(rangeFeeder);
            scanner.init();
            ScanningSubject scanningSubject = new ScanningSubject(address);
            scanningSubject.addRequestedPort(80);
            scanningSubject.addRequestedPort(81);
            scanningSubject.addRequestedPort(443);
            ScanningResult scanningResult = new ScanningResult(address, fetchers.size());
            scanningResult.resultList = scanningResultList;
            scanner.scan(scanningSubject, scanningResult);
            System.out.println(scanningResult);
*/

            rangeFeederFinite = new RangeFeeder("192.168.1.1", "192.168.1.30");

            scanningResultList.initNewScan(rangeFeederFinite);

            scanner.init();
            while(rangeFeederFinite.hasNext()) {

                //address = InetAddress.getByName("192.168.1.3");

                //scanningSubject = new ScanningSubject(address);
                scanningSubject = rangeFeederFinite.next();
                scanningSubject.addRequestedPort(80);
                scanningSubject.addRequestedPort(443);
                scanningSubject.addRequestedPort(445);
                scanningResult = new ScanningResult(scanningSubject.getAddress(), fetchers.size());
                scanningResult.setFetchers(scanningResultList.getFetchers());
                //scanningResult.resultList = scanningResultList;
                scanner.scan(scanningSubject, scanningResult);
                System.out.println(scanningResult);
                if (resultList.get(scanningSubject.getAddress()) != null && resultList.get(scanningSubject.getAddress()).getType() != scanningResult.getType()) {
                    System.out.println("changed");

                    String file = "/home/michele/Downloads/mixkit-system-beep-buzzer-fail-2964.wav";
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile());
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    //clip.start();
                }
                resultList.put(scanningSubject.getAddress(), scanningResult);

            }
            scanner.cleanup();
/*
            address = InetAddress.getByName("192.168.1.114");
            rangeFeeder = new RangeFeederFinite("192.168.1.114", "192.168.1.114");
            scanningResultList.initNewScan(rangeFeeder);
            scanner.init();
            scanningSubject = new ScanningSubject(address);
            scanningSubject.addRequestedPort(80);
            scanningSubject.addRequestedPort(8080);
            scanningSubject.addRequestedPort(443);
            scanningResult = new ScanningResult(address, fetchers.size());
            scanningResult.resultList = scanningResultList;
            scanner.scan(scanningSubject, scanningResult);
            System.out.println(scanningResult);
*/
        } catch (ClassNotFoundException | UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
