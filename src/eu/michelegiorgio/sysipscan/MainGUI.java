package eu.michelegiorgio.sysipscan;

import eu.michelegiorgio.sysipscan.gui.MainWindow;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.net.CombinedUnprivilegedPinger;
import net.azib.ipscan.core.net.JavaPinger;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.fetchers.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class MainGUI {

	private static MainWindow mainWindow;
	private static ScanningResultList scanningResultList;
	private static Scanner scanner;
	private static int numberOfFetchers;

	public static void main(String... args) {
		mainWindow = new MainWindow();
		mainWindow.init();

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					init();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				ThreadScanning threadScanning = new ThreadScanning(mainWindow, scanner, numberOfFetchers, scanningResultList);
				Thread thread = new Thread(threadScanning);
				thread.start();
			}
		};
		mainWindow.addActionListenerToButtonStart(actionListener);
	}

	private static void init() throws ClassNotFoundException {
		Preferences prefs = Preferences.userRoot().node("main");
		List<Fetcher> fetchers = new ArrayList<>();
		ScannerConfig scannerConfig = new ScannerConfig(prefs);
		PingerRegistry pingerRegistry = new PingerRegistry(scannerConfig);

		Pinger javaPinger = new JavaPinger(45);
		Pinger combinedPinger = new CombinedUnprivilegedPinger(45);
		scannerConfig.selectedPinger = "pinger.combined";
		scannerConfig.pingCount = 6;

		fetchers.add(new IPFetcher());
		fetchers.add(new PingFetcher(pingerRegistry, scannerConfig, combinedPinger));
		fetchers.add(new HostnameFetcher());
		fetchers.add(new PortsFetcher(scannerConfig));
		fetchers.add(new UnixMACFetcher());
		fetchers.add(new MACVendorFetcher(new UnixMACFetcher()));

		FetcherRegistry fetcherRegistry = new FetcherRegistry(fetchers, prefs);
		String[] arrayIDFetchers = {IPFetcher.ID, PingFetcher.ID, HostnameFetcher.ID, PortsFetcher.ID,
				UnixMACFetcher.ID, MACVendorFetcher.ID};
		fetcherRegistry.updateSelectedFetchers(arrayIDFetchers);
		numberOfFetchers = fetchers.size();
		scanningResultList = new ScanningResultList(fetcherRegistry);
		scanner = new Scanner(fetcherRegistry);
	}

}
