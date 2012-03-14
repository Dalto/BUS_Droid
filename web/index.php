<?php
	
	$dbName = "RTC_DROID.db";

	$action = $_GET['action'];

	if (strcasecmp($action, "download") == 0 || strcasecmp($action, "update") == 0) {
		//header("Content-Type: application/octet-stream");
		//include('./' . $dbName);	
		header('Location: ./' . $dbName);
	}	
	else if (strcasecmp($action, "checkVersion") == 0) {
		header("Content-Type: text/plain");
		echo "1";
	}
	else {
		echo "Error happen";
	}



?>
