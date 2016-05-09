#!/bin/sh

for f in `find content -type f` ; do
	sed -i $f \
		-e 's:<code> : <code>:g' \
		-e 's: </code>:</code> :g' \
		-e 's:</code><code>::g' \
		-e 's:<code></code>::g'
done
