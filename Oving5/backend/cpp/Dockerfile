FROM gcc:latest

RUN mkdir /cpp

WORKDIR /cpp

COPY main.cpp /cpp/

RUN g++ -o Test main.cpp

CMD [ "./Test" ]
