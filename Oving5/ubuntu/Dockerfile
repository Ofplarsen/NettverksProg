FROM ubuntu:latest

RUN apt-get -y update && apt-get install -y

RUN apt-get -y install clang

#RUN apt-get install manpages-dev

#RUN gcc --version

COPY . /usr/src/cpp_test

WORKDIR /usr/src/cpp_test

RUN clang++ -o Test main.cpp

CMD [ "./Test" ]
