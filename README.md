# FreeOTP Extractor

A quick and dirty tool to take the backup XML file from FreeOTP and convert the tokens stored within to QR codes/OTP urls.

Released under the Apache 2.0 license -- please note that several files are taken from different sources, so their individual copyright notices must be maintained.

## Usage

> target/freeotp-extractor-1.0-SNAPSHOT.jar <backup xml (in sp folder)>

URLs will be output to the command line, and QR codes will be saved to a `images/` folder in the current directory. This could be made configurable but I stopped once I got far enough to get my tokens back.

## Known Issues

There are many -- please fix them if you care

- output directory is not configurable
- HOTP tokens are not supported (the tURI method in Token was using Android classes, so it was very quickly rewritten. i do not use HOTP tokens so I didn't take the time to fix them.)
- Very limited error handling. Be ready for stacktraces.
- File names may contain invalid characters on Windows
