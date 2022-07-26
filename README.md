# Autosplitter for Nightmare and Phosani's Nightmare
All of the credit for this plugin deserves to go to De0 and SkyBouncer. I saw what Sky did to utilize De0's [CoX Timers](https://github.com/dey0/pluginhub-plugins/tree/cox-additions/src/main/java/de0/coxtimers) and make [CM AutoSplitter](https://github.com/SkyBouncer/cmAutoSplitter) so I converted it to work for De0's [NM Timers Plugin](https://github.com/dey0/pluginhub-plugins/tree/nightmare-timers/src/main/java/de0/nmtimers).

Sends a split when nightmare spawns, and everytime the npc id changes of nightmare. 

Using this plugin requires the LiveSplit program with the LiveSplit server component.

Installation and setup guide can be found here:

[LiveSplit](https://livesplit.org/downloads/)

[LiveSplit Server](https://github.com/LiveSplit/LiveSplit.Server)

## How to use
Download LiveSplit and the LiveSplit server component.

Turn the plugin on and make sure the port in the plugin settings match your LiveSplit server port.

![config](https://user-images.githubusercontent.com/109918307/181096907-edfdcd47-d463-4dbe-9ef3-599bbfbe8cbc.png)


Start LiveSplit and start the LiveSplit server (right click LS -> control -> start server).
Make sure to add the LS server to your layout, otherwise you won't see "start server" under control.

![lsserver](https://user-images.githubusercontent.com/109918307/181097112-0a92fdf9-4f81-4bed-a5f6-56bcd70a5639.png)


Open the sidebar and click "Connect".
If the status turns green it means you have a connection to your LiveSplit server.
If it stays red something went wrong, most likely you did not start the LiveSplit server
or you have mismatching ports in the plugin settings and the LiveSplit server settings.

![sidebar](https://user-images.githubusercontent.com/109918307/181097259-e5aa2fb7-f728-427e-a48b-ccd3293f0ff9.png)


If your status is green you are good to go.


## Templates
Layout and splits templates for LiveSplit can be found in [LiveSplit templates]
