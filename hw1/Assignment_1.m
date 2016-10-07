clear,clc,close all
% Loading score data
DATA_SCORE = dlmread('data.score.txt');
ID = DATA_SCORE(:,1);
MID = DATA_SCORE(:,2);
FINAL = DATA_SCORE(:,3);

%% Question 1
% a) Max and Min
maxMid = max(MID)
minMid = min(MID)

% b) Quantile
quantileMid = quantile(MID,[.25 .50 .75])

% c) Mean
meanMid = mean(MID)

% d) Mode
modeMid = mode(MID)

% e) Variance
varMid = var(MID)

% f) Distribution
figure(),hist(MID)
x1 = meanMid - modeMid
x2 = meanMid - quantileMid(2) % NO

%% Question 2
% a) variance before and after
varFinal = var(FINAL)
ZFinal = zscore(FINAL);
varZF = var(ZFinal)

% b) 90 after normalization
is90 = find(FINAL==90);
Z_90 = ZFinal(is90(1))

%% Question 3
% a) Pearson's correlation coefficient
Pearson = corr(MID, FINAL)

% b) Covariance
Comat = cov(MID, FINAL);
Covar = Comat(1,2)

%% Question 4
clear,clc,close all
% Loading supermarket data
DATA_SUPERMARKET = dlmread('data.supermarkets.txt','\t', 1, 1);
JS = DATA_SUPERMARKET(1,1:100);
KK = DATA_SUPERMARKET(2,1:100);

% Jaccard coefficient
q = 107; r = 31; s = 19;
Jaccard = q / (q+r+s)

% Minkowski Distance
h1 = pdist([JS;KK], 'cityblock')
h1 = sum(abs(JS-KK));

h2 = pdist([JS;KK], 'euclidean')
h2 = sqrt(sum((JS-KK).^2));

h3 = pdist([JS;KK], 'chebychev')
h3 = max(abs(JS-KK));

% Cosine similarity 
h4 = 1 - pdist([JS;KK], 'cosine') % One minus the cosine of the included angle between points (treated as vectors).
h4 = JS*KK'/ (norm(JS)*norm(KK))

% KL divergence
pJS = JS./sum(JS(:));
pKK = KK./sum(KK(:));
KL = 0;
for i=1:size(JS,2)
    KL = KL + pJS(i)* log(pJS(i)/pKK(i));
end
KL

%% Question 5
clear,clc,close all
O = [1346,430; 133,32974];

N = sum(O(:));
R1 = sum(O(1,:))/N;
R2 = sum(O(2,:))/N;
C1 = sum(O(:,1))/N;
C2 = sum(O(:,2))/N;
E = [R1*C1, R1*C2; R2*C1, R2*C2]*N;

S = (O-E).^2./E;
chi2 = sum(S(:))


























