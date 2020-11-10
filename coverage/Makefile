SBT = sbt
FLAGS = --error

.PHONY : all
all : aluaccu

aluaccu :
	@$(SBT) $(FLAGS) compile

.PHONY : test
test : 
	@echo "Generating coverage results."
	@$(SBT) $(FLAGS) clean coverage test
	@echo "Done. Compiling coverage report."
	@$(SBT) $(FLAGS) coverageReport
	@echo "Report completed!"

.PHONY : clean
clean:
	@echo "Clearing auto-generated folders."
	@if [ -d "./generated" ]; then rm -rf ./generated/*; rmdir ./generated; fi
	@if [ -d "./test_run_dir" ]; then rm -rf ./test_run_dir/*; rmdir ./test_run_dir; fi
	@echo "Cleaning the project."
	@$(SBT) clean
	@if [ -d "./project/target" ]; then rm -rf ./project/target/*; rmdir ./project/target; fi
	@if [ -d "./project/project" ]; then rm -rf ./project/project/*; rmdir ./project/project; fi
	@if [ -d "./target" ]; then rm -rf ./target/*; rmdir ./target; fi
	@echo "Folder cleaned!"
