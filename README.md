# Sentiment-Analysis-of-Movies
 How  to run: 
-------------------------------------------------------------
-------------------------------------------------------------
 If running on console:
 

 1.Save NBTrain.java and NBTest.java on disk .
 2.Compile the java file by giving below command: 


 javac NBTrain.java 
 javac NBTest.java

 
 3. Run by giving the command 
 java NBtrain <location of the train data> <File Path of model file>
 java NBTest <Model File> <Test File location> 
 
 Results are generated at Model file folder

Assumption:
Train data should have two folders as "pos" and "neg" in train data folder.
Dev data is also classified and present in two folders as per thier sentiment.
 
** The other projects on binary and bi-gram classifcation can also be compiled and executed in the same way.
-----------------------------------------------------------------
------------------------------------------------------------

 If running on eclipse:
 
1. Import the HW6 package
2. Give the command line argument through Run->Configuration->Command Line Argument as 
   <location of the train data> <File Path of model file>
3. Run


-----------------------------------------------------------
Test Results:
-----------------------------------------------------------

Results on Dev Data:

- dev_result inside Result contain the classification of dev data Classifier_<MM-dd-yy_HH-mm-ss> file.
  Every row contains doc name, positive score, negative score, prediciton by classifier, given classification
- test_result inside Result contain the classification of test data as Classifier_<MM-dd-yy_HH-mm-ss> file
  Every row contains doc name, positive score, negative score, prediciton by classifier
- Model.txt is the model file generated on train data
  The first line of prediction file contains the metadata. Next each row contains terms, term frequency in pos class and term freq in neg class
- PosToNeg_<MM-dd-yy_HH-mm-ss> file contains the terms soretd by positive to negative log ratio of term weights
  this contains the term, ratio, pos frequency and neg frequency
- NegToPos_<MM-dd-yy_HH-mm-ss> file contains the terms soretd by negative to positive log ratio of term weights
  this contains the term, ratio, pos frequency and neg frequency
- Top20PosToNeg file contains the top 20 words sorted on positive to negative log ratio of term weights
  this contains the term, ratio, pos frequency and neg frequency
- Top20NegToPos file contains the top 20 words sorted on negative to positive log ratio of term weights
  this contains the term, ratio, pos frequency and neg frequency


Percentage of correct classifcation in dev data wrt to classifer prediction:
Positive Percentage Classification: 73%
Negative Percentage Classification: 84%

Classification of test data:
Positives: 86
Negatives: 114



***************************
Model Modification
**************************

These solutions are stored in "Modified Version" Folder

For modified version we used two solutions:
1. Using Binary relevance with Laplace smoothing
2. Using Bi-gram Model with Laplace Smoothing



Proposed Modification:
We used Binary Bayesian Classifier instead of multinomial classifier and found that terms to which it assigns highest probality 
scores do signify the positive and negative sentiments.
Similary we used Bi-gram model but found that it tends to align towards negative probablity , also the top words predicted for 
positive and negative words do not specify and sentiments as the terms have less occurances as a combination across collection or 
they form a term with a punctuation which reduces the relevance of the word.

The comparision between each of the solution are described below on dev data:

                           Neg%        Pos%            Top Negative Words               	Top Positive Words
Multinomial Classifier:	   84		 	73            [ &nbsp,jolie,seagal,brenner ]       [shrek,mulan,gattaca,flynt]

Binary Classifier     :    57           95            [ Bad, worst , boring, stupid]       [life,both,world,great,perfect,best,performance]

Bi-gram Classifier    :    99           55         [&nbsp; ,.&nbsp, 'this mess', mars "]   [black cauldron , drunken master, "chicken, story2]

Also the classification by the binary classifier and bi-gram classifier are as below:

Binary classifier:
Positives: 143
Negatives: 57

Bi-gram classifier:
Positives: 47
Negatives: 153

After analysing we found that Binary Classifier tends to outperform n-grams classifier if the language used is straightforward and use proper sentiment terms.
For example the below sentence will be classified as positive although its negative by binary classifier.
For ex: I was expecting the movies to be great and awesome but it was not so good.
Also the results shows that Multinomial Bayes Classification contains highest weights of many terms which
does not signify any sentiments such as &nbsp.  This polarity is due to frequency associated with each terms.
