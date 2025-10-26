# Code Judge (local recreate)

Run `recreate_files.ps1` to recreate project structure. After running:

- Build judge image: `docker build -t judge-java:latest D:\shodai`
- Build backend: `mvn package` (requires Maven installed)
- Run frontend: `cd D:\shodai\frontend` then `npm install` and `npm run dev`
