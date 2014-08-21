


	$filename = sprintf("legal-%02i.dcf", 1);
	$command = sprintf("java Decaf ../semantics/%s", $filename);
	$result = `$command`;
	print sprintf("%s: ", $filename);
	if ($result =~ /Yay/) {
		print "semantic did pass. :)";
	} elsif ($result =~ /Exception/) {
		print "syntax did not pass. :(";
	} elsif ($result =~ /Error/) {
		print "syntax error. :/";
	} else {
		print "some weird crap happened.";
	}
	print "\n";


for $i (1..17) {
	$filename = sprintf("illegal-%02i.dcf", $i);
	$command = sprintf("java Decaf ../semantics/%s", $filename);
	print sprintf("%s: \n", $filename);
	$result = `$command`;
	if ($result =~ /Semantic errors/) {
		print "semantic errors :)";
	} else {
		print "some weird crap happened.";
	}
	print "\n";
}
