

for $i (1..18) {
	$filename = sprintf("legal-%02i", $i);
	$command = sprintf("java Decaf ../input/%s", $filename);
	$result = `$command 2>/dev/null`;
	print sprintf("%s: ", $filename);
	if (($result =~ /Yay/) or ($result =~ /Semantic errors/)) {
		print "syntax did pass. :)";
	} elsif ($result =~ /Exception/) {
		print "syntax did not pass. :(";
	} elsif ($result =~ /Error/) {
		print "syntax error. :/";
	} else {
		print "some weird crap happened.";
	}
	print "\n";
}

for $i (1..20) {
	$filename = sprintf("illegal-%02i", $i);
	$command = sprintf("java Decaf ../input/%s", $filename);
	$result = `$command 2> /dev/null`;
	print sprintf("%s: ", $filename);
	if (($result =~ /Yay/) or ($result =~ /Semantic errors/)) {
		print "syntax did pass. (ERROR) :(";
	} elsif ($result =~ /Exception/) {
		print "syntax did not pass. :)";
	} elsif ($result =~ /Error/) {
		print "syntax error. :/";
	} else {
		print "some weird crap happened.";
	}
	print "\n";
}
