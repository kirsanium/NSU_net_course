# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.13

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /home/kirsanium/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/183.5153.40/bin/cmake/linux/bin/cmake

# The command to remove a file.
RM = /home/kirsanium/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/183.5153.40/bin/cmake/linux/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/kirsanium/CLionProjects/net_lab6

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/kirsanium/CLionProjects/net_lab6/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/net_lab6.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/net_lab6.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/net_lab6.dir/flags.make

CMakeFiles/net_lab6.dir/main.cpp.o: CMakeFiles/net_lab6.dir/flags.make
CMakeFiles/net_lab6.dir/main.cpp.o: ../main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/net_lab6.dir/main.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/net_lab6.dir/main.cpp.o -c /home/kirsanium/CLionProjects/net_lab6/main.cpp

CMakeFiles/net_lab6.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/net_lab6.dir/main.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kirsanium/CLionProjects/net_lab6/main.cpp > CMakeFiles/net_lab6.dir/main.cpp.i

CMakeFiles/net_lab6.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/net_lab6.dir/main.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kirsanium/CLionProjects/net_lab6/main.cpp -o CMakeFiles/net_lab6.dir/main.cpp.s

CMakeFiles/net_lab6.dir/PortForwarder.cpp.o: CMakeFiles/net_lab6.dir/flags.make
CMakeFiles/net_lab6.dir/PortForwarder.cpp.o: ../PortForwarder.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/net_lab6.dir/PortForwarder.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/net_lab6.dir/PortForwarder.cpp.o -c /home/kirsanium/CLionProjects/net_lab6/PortForwarder.cpp

CMakeFiles/net_lab6.dir/PortForwarder.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/net_lab6.dir/PortForwarder.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kirsanium/CLionProjects/net_lab6/PortForwarder.cpp > CMakeFiles/net_lab6.dir/PortForwarder.cpp.i

CMakeFiles/net_lab6.dir/PortForwarder.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/net_lab6.dir/PortForwarder.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kirsanium/CLionProjects/net_lab6/PortForwarder.cpp -o CMakeFiles/net_lab6.dir/PortForwarder.cpp.s

CMakeFiles/net_lab6.dir/Connection.cpp.o: CMakeFiles/net_lab6.dir/flags.make
CMakeFiles/net_lab6.dir/Connection.cpp.o: ../Connection.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object CMakeFiles/net_lab6.dir/Connection.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/net_lab6.dir/Connection.cpp.o -c /home/kirsanium/CLionProjects/net_lab6/Connection.cpp

CMakeFiles/net_lab6.dir/Connection.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/net_lab6.dir/Connection.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kirsanium/CLionProjects/net_lab6/Connection.cpp > CMakeFiles/net_lab6.dir/Connection.cpp.i

CMakeFiles/net_lab6.dir/Connection.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/net_lab6.dir/Connection.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kirsanium/CLionProjects/net_lab6/Connection.cpp -o CMakeFiles/net_lab6.dir/Connection.cpp.s

CMakeFiles/net_lab6.dir/Server.cpp.o: CMakeFiles/net_lab6.dir/flags.make
CMakeFiles/net_lab6.dir/Server.cpp.o: ../Server.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object CMakeFiles/net_lab6.dir/Server.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/net_lab6.dir/Server.cpp.o -c /home/kirsanium/CLionProjects/net_lab6/Server.cpp

CMakeFiles/net_lab6.dir/Server.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/net_lab6.dir/Server.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kirsanium/CLionProjects/net_lab6/Server.cpp > CMakeFiles/net_lab6.dir/Server.cpp.i

CMakeFiles/net_lab6.dir/Server.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/net_lab6.dir/Server.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kirsanium/CLionProjects/net_lab6/Server.cpp -o CMakeFiles/net_lab6.dir/Server.cpp.s

CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.o: CMakeFiles/net_lab6.dir/flags.make
CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.o: ../SOCKS5Connecter.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building CXX object CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.o -c /home/kirsanium/CLionProjects/net_lab6/SOCKS5Connecter.cpp

CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kirsanium/CLionProjects/net_lab6/SOCKS5Connecter.cpp > CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.i

CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kirsanium/CLionProjects/net_lab6/SOCKS5Connecter.cpp -o CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.s

CMakeFiles/net_lab6.dir/DNSResolver.cpp.o: CMakeFiles/net_lab6.dir/flags.make
CMakeFiles/net_lab6.dir/DNSResolver.cpp.o: ../DNSResolver.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Building CXX object CMakeFiles/net_lab6.dir/DNSResolver.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/net_lab6.dir/DNSResolver.cpp.o -c /home/kirsanium/CLionProjects/net_lab6/DNSResolver.cpp

CMakeFiles/net_lab6.dir/DNSResolver.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/net_lab6.dir/DNSResolver.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kirsanium/CLionProjects/net_lab6/DNSResolver.cpp > CMakeFiles/net_lab6.dir/DNSResolver.cpp.i

CMakeFiles/net_lab6.dir/DNSResolver.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/net_lab6.dir/DNSResolver.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kirsanium/CLionProjects/net_lab6/DNSResolver.cpp -o CMakeFiles/net_lab6.dir/DNSResolver.cpp.s

# Object files for target net_lab6
net_lab6_OBJECTS = \
"CMakeFiles/net_lab6.dir/main.cpp.o" \
"CMakeFiles/net_lab6.dir/PortForwarder.cpp.o" \
"CMakeFiles/net_lab6.dir/Connection.cpp.o" \
"CMakeFiles/net_lab6.dir/Server.cpp.o" \
"CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.o" \
"CMakeFiles/net_lab6.dir/DNSResolver.cpp.o"

# External object files for target net_lab6
net_lab6_EXTERNAL_OBJECTS =

net_lab6: CMakeFiles/net_lab6.dir/main.cpp.o
net_lab6: CMakeFiles/net_lab6.dir/PortForwarder.cpp.o
net_lab6: CMakeFiles/net_lab6.dir/Connection.cpp.o
net_lab6: CMakeFiles/net_lab6.dir/Server.cpp.o
net_lab6: CMakeFiles/net_lab6.dir/SOCKS5Connecter.cpp.o
net_lab6: CMakeFiles/net_lab6.dir/DNSResolver.cpp.o
net_lab6: CMakeFiles/net_lab6.dir/build.make
net_lab6: CMakeFiles/net_lab6.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_7) "Linking CXX executable net_lab6"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/net_lab6.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/net_lab6.dir/build: net_lab6

.PHONY : CMakeFiles/net_lab6.dir/build

CMakeFiles/net_lab6.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/net_lab6.dir/cmake_clean.cmake
.PHONY : CMakeFiles/net_lab6.dir/clean

CMakeFiles/net_lab6.dir/depend:
	cd /home/kirsanium/CLionProjects/net_lab6/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/kirsanium/CLionProjects/net_lab6 /home/kirsanium/CLionProjects/net_lab6 /home/kirsanium/CLionProjects/net_lab6/cmake-build-debug /home/kirsanium/CLionProjects/net_lab6/cmake-build-debug /home/kirsanium/CLionProjects/net_lab6/cmake-build-debug/CMakeFiles/net_lab6.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/net_lab6.dir/depend

