#!/usr/bin/env bash
# Keep the GitBook copy of the guides/images in sync with the repo docs.
set -euo pipefail
cd "$(dirname "$0")/../.."

for f in architecture breakpoints foldables components view-tier publishing testing; do
  cp "docs/$f.md" "gitbook/docs/$f.md"
done
mkdir -p gitbook/docs/images
cp docs/images/*.png gitbook/docs/images/

python3 gitbook/scripts/gen_api_docs.py
echo "GitBook guides synced; API docs regenerated."
