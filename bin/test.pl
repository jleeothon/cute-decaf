

for $i (1..20) {
	$filename = sprintf("illegal-%02i", $i);
	$command = sprintf("java DecafParser input/%s", $filename);
	$result = `$command`;
	print sprintf("%s: ", $filename);
	if ($result =~ /Yay/) {
		print "did pass. (ERROR)";
	} elsif ($result =~ /Exception/) {
		print "did not pass. :)";
	} elsif ($result =~ /Error/) {
		print "error. :/";
	} else {
		print "some weird crap happened.";
	}
	print "\n";
}


for $i (1..18) {
	$filename = sprintf("legal-%02i", $i);
	$command = sprintf("java DecafParser input/%s", $filename);
	$result = `$command`;
	print sprintf("%s: ", $filename);
	if ($result =~ /Yay/) {
		print "did pass. :)";
	} elsif ($result =~ /Exception/) {
		print "did not pass. :(";
	} elsif ($result =~ /Error/) {
		print "error. :/";
	} else {
		print "some weird crap happened.";
	}
	print "\n";
}