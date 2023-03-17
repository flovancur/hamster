# Compile with gcc
CC=gcc
# Command to auto-generate dependencies with gcc
#DEPCC=$(CC) -MM

# Additional flags to pass to gcc:
# -g : generate debug information so we can use gdb to debug
# -I <dir> : look in directory <dir> for additional .h files
CFLAGS=-g -Iinclude
# all warnings + warnings are considered as errors
CFLAGS += -Wall -Werror

# Linker options: link program against libhamster.a
# to be found in directory ../libsrc
LDLIBS=-Llibsrc -lhamster
LIB=libsrc/libhamster.a

# compile all .c files found in src directory
FILES=$(shell find src -name '*.c')

BINARY = hamster

OBJS=$(FILES:.c=.o)
#DEPS=.depend

all: $(BINARY)


#-include $(DEPS)

$(BINARY): $(OBJS) $(LIB)
	$(CC) $^ -o $@ $(LDLIBS) $(LDFLAGS)

$(LIB):
	cd libsrc;$(MAKE)

#$(DEPS):
#	$(DEPCC) $(FILES) >$(DEPS)

clean:
	rm -f $(OBJS) $(BINARY) 
#$(DEPS)

distclean: clean
	cd libsrc;$(MAKE) distclean
	rm -f $(PROGS) *~

test: $(BINARY)
	cd scripts; ./testhamster.sh
