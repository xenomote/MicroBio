all:
	mkdir out
	javac -d out -cp lib/*:. $$(find . -name "*.java")

run:
	java -cp lib/*:out game.Game

clean:
	rm -rf out