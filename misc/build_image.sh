docker build -t microtek:1.0.0 .
docker tag microtek:1.0.0 lakshannv/microtek:1.0.0
docker image push lakshannv/microtek:1.0.0
docker image rm lakshannv/microtek:1.0.0
docker image rm microtek:1.0.0