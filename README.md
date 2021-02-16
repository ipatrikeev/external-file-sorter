# external-sorter #

## General info ##
External sorting java implementation 

For more information about the algorithm see:
* https://en.wikipedia.org/wiki/External_sorting
* https://en.wikipedia.org/wiki/K-way_merge_algorithm

## Prerequisites ##
* Java 8

## Usage
1. Build the project:
 
    ```bash
    ./gradlew build
    ``` 

2. Generate a file using helper tool (optional)
 
    ```bash
    ./generate-file.sh {number of lines} {max line length} {file path}
    ```
   
    Example:
    ```bash
    ./generate-file.sh 10000 300 /tmp/random-file.txt
    ```
   
3. Sort a file

    ```bash
    ./external-sort.sh /tmp/random-file.txt
   ```
   
4. Validate the file is sorted (optional)

    ```bash
    ./validate.sh /tmp/random-file.txt 
   ```
   
## Tuning the sorter ##
There are 4 main parameters for the sorter app:
* file path to sort
* number of workers (4 by default) - the number of workers which sorts file chunks and process preliminary merging
* file block size in KB (512 by default) - file chunk size for partial sorting 
* limit for number of files used in merge phase (250 by default). If the file amount is greater than this limit, the files will be merged into temporary files before being merged into the destination one

The default values are located in the helper script file. See [external-sort.sh](external-sort.sh)

You could also change the used memory either in the helper script or executing the jar directly