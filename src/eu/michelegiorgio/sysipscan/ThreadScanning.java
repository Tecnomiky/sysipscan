package eu.michelegiorgio.sysipscan;

import eu.michelegiorgio.sysipscan.gui.MainWindow;
import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.feeders.RangeFeeder;

import java.util.List;

public class ThreadScanning implements Runnable {

	private MainWindow mainWindow;
	private Scanner scanner;
	private int numberOfFetchers;
	private ScanningResultList scanningResultList;

	public ThreadScanning(MainWindow mainWindow, Scanner scanner, int numberOfFetchers, ScanningResultList scanningResultList) {
		this.mainWindow = mainWindow;
		this.scanner = scanner;
		this.numberOfFetchers = numberOfFetchers;
		this.scanningResultList = scanningResultList;
	}

	@Override
	public void run() {
		ScanningSubject scanningSubject;
		ScanningResult scanningResult;

		String startIP = mainWindow.getStartIP();
		String endIP = mainWindow.getEndIP();
		RangeFeeder rangeFeeder = new RangeFeeder(startIP, endIP);
		scanningResultList.initNewScan(rangeFeeder);

		scanner.init();
		while (rangeFeeder.hasNext()) {
			scanningSubject = rangeFeeder.next();
			scanningSubject.addRequestedPort(22);
			scanningSubject.addRequestedPort(80);
			scanningSubject.addRequestedPort(443);
			scanningSubject.addRequestedPort(445);

			scanningResult = new ScanningResult(scanningSubject.getAddress(), numberOfFetchers);
			scanningResult.setFetchers(scanningResultList.getFetchers());

			scanner.scan(scanningSubject, scanningResult);

			List<Object> resultValues = scanningResult.getValues();
			mainWindow.addRow(new String[] {resultValues.get(0).toString(),
					resultValues.get(1).toString().equals("notAvailable") ? "" : resultValues.get(1).toString(),
					resultValues.get(2).toString().equals("notScanned") ? "" : resultValues.get(2).toString(),
					resultValues.get(4).toString().equals("notScanned") ? "" : resultValues.get(4).toString(),
					resultValues.get(3).toString().equals("notScanned") ? "" : resultValues.get(3).toString() }
			);
			mainWindow.addMacAddressTooltip(resultValues.get(5).toString());
			mainWindow.setStatus("Scanning "+rangeFeeder.percentageComplete()+"%");
		}
		scanner.cleanup();
		mainWindow.setStatus("Scan ended");
	}
}
