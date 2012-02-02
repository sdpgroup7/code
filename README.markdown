Main repository for SDP group 7 for academic year 2011 - 2012

How to run / compile the PC code (make sure the brick is running the Nxt_code if you want to connect via bluetooth)
--
Low-tech people:
1. cd PC/
(2. ant clean)
3. ant
4. ant MainRunner 

Hi-tech people:
1. open Eclipse
2. File->Import...
3. Existing Projects into Workspace
4. Select root directory: to PC
5. check project PC
6. untick "Copy projects into workspace"
7. right click on PC-src: Run As->Run Configurations...
8. under Environment tab, click on New...
Name: LD_LIBRARY_PATH
Value: /group/teaching/sdp/sdp7/lejos/bluez/lib:/group/teaching/sdp/sdp7/lib
9. Run
(Note: you still need to commit in the command line, Eclipse git plug-in is strange and doesn't allow no-ff merge)
