clc; clear;
%% Data input
DATA = ...
    [1, 8.2, 6.4;
     2, 0.1, 6.7;
     3, 1.5, 7.8;
     4, 2.2, 3.4;
     5, 1.6, 3.5;
     6, 4.3, 9.7;
     7, 2.5, 2.2;
     8, 6.2, 3.1;
     9, 5.5, 0.3];
% CENTER = ...
%     [1, 2.0, 5.0;
%      2, 6.0, 0.5;
%      3, 6.0, 5.0];
 
 CENTER = ...
    [1, 1.0, 4.0;
     2, 0.2, 6.0;
     3, 4.3, 2.0];
 
assignRecord = {[],[],[]};
centerRecord = CENTER;
%% Assign Center
centerChange = 1;
numIter = 0;
while(centerChange~=0)
    % assign center
    for i = 1:size(DATA,1)
        thisData = DATA(i,:);

        minDist = 10000000000000000;
        closestCenter = 0;
        for c = 1:size(CENTER,1)
            thisCenter = CENTER(c,:);
            disToThisCenter = dist2(thisData, thisCenter);
            if (minDist > disToThisCenter)
                minDist = disToThisCenter;
                closestCenter = thisCenter(:,1);
            end
        end
        assignRecord{closestCenter} =  [assignRecord{closestCenter}, thisData(:,1)];
    end
    assignRecord{1}
    assignRecord{2}
    assignRecord{3}

    % Update Center
    for i = 1:3
        pointInCluster = assignRecord{i};
        newCenter = sum(DATA(pointInCluster,:),1)./size(pointInCluster,2);
        newCenter(:,1) = i;

        centerSet(i,:) = newCenter;
    end
    centerRecord = {centerRecord, centerSet};
    centerChange = sum(sum(CENTER - centerSet))
    
    CENTER = centerSet
    assignRecord = {[],[],[]};
    numIter = numIter+1
    clc;
end
