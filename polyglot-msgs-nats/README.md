WIP
beware!

find /c/Data/enron/maildir/ -maxdepth 3 -type f | grep sent | awk 'BEGIN {srand()} !/^$/ { if (rand() <= .01) print $0}' | xargs head -50 | ./fwd_nats.exe