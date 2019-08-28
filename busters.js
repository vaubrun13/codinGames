/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 **/

var bustersPerPlayer = parseInt(readline()); // the amount of busters you control
var ghostCount = parseInt(readline()); // the amount of ghosts on the map
var myTeamId = parseInt(readline()); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right
var DISTANCETORELEASE = 1600;
var MAXDISTANCETOBUST = 1760;
var MINDISTANCETOBUST = 900;
var DISTANCETOSTUN = 1760;
var FOGDISTANCE = 2200;
var corners = [{x: 16000, y: 0}, {x: 0, y: 9000}];
var randCorner = 0;

class Buster {
    constructor(id, x, y, state, value) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.state = state;
        this.turnBeforStun = 0;
        this.ghostCarried = value;
        this.chasedGhost = null;
        this.cornerUsed = chooseCorner();
        this.lastAction = '';
    }


    findClosest(array) {

        if (array.length === 0) {
            return null;
        }
        var closest = array[0];
        for (var i = 1; i < array.length; i++) {

            if (calculateDistance(this.x, this.y, array[i].x, array[i].y) < calculateDistance(this.x, this.y, closest.x, closest.y)) {
                closest = array[i];
            }
        }

        return closest;
    }

    findClosestGhost(array) {
        // busterId = busterId | -1; 
        if (array.length === 0) {
            return null;
        }
        var closest = array[0];
        for (var i = 1; i < array.length; i++) {

            if (calculateDistance(this.x, this.y, array[i].x, array[i].y) < calculateDistance(this.x, this.y, closest.x, closest.y)) {
                closest = array[i];
            }
        }

        return closest;
    }

    getAction(ghostArray, opponentBusters) {
        var action;
        var closestGhost;
        if (this.state === 1) {
            if (calculateDistance(this.x, this.y, baseX, baseY) < DISTANCETORELEASE) {
                action = 'RELEASE';
                allGhosts.splice(getGhostIndex(this.ghostCarried), 1);
                this.chasedGhost = null;

            } else {
                action = 'MOVE ' + baseX + ' ' + baseY;
            }
        } else {
            var closestGhost;
            if (this.chasedGhost !== null) {
                // var chasingGhostIdx = getGhostIndex(this.chasedGhost);

                // if (chasingGhostIdx !== -1) {
                //      closestGhost = allGhosts[chasingGhostIdx];
                // } else {
                closestGhost = this.findClosestGhost(ghostArray);
                // if(closestGhost === null || (closestGhost.chasedBy !== this.id && closestGhost.chasedBy !== null)){
                //     closestGhost = null;
                // }
                // }

                printErr('Chasing ' + this.chasedGhost);
            } else {
                closestGhost = this.findClosestGhost(ghostArray);
                // if(closestGhost === null || (closestGhost.chasedBy !== this.id && closestGhost.chasedBy !== null)) {
                //     closestGhost = null;
                // }
            }
            var closestBuster = this.findClosest(opponentBusters);

            if (closestGhost === null || typeof closestGhost === 'undefined') {

                var cornerToGoTo = this.cornerUsed;

                if (calculateDistance(this.x, this.y, cornerToGoTo.x, cornerToGoTo.y) < FOGDISTANCE) {
                    this.cornerUsed = chooseCorner();

                }
                action = 'MOVE ' + cornerToGoTo.x + ' ' + cornerToGoTo.y;
            } else {
                printErr('Hunting ' + closestGhost.id);
                // closestGhost.chasedBy = this.id;
                // this.chasedGhost = closestGhost.id;
                var distanceToGhost = calculateDistance(this.x, this.y, closestGhost.x, closestGhost.y);

                if (state !== 1 && (distanceToGhost < MAXDISTANCETOBUST && distanceToGhost >= MINDISTANCETOBUST)) {
                    action = 'BUST ' + closestGhost.id;
                    printErr(action);


                }
                // if(state === 0 && (distanceToGhost < MAXDISTANCETOBUST && distanceToGhost >= MINDISTANCETOBUST) && closestGhost.visible === false)  {
                //     action = 'MOVE ' + otherBaseX + ' ' + otherBaseY;

                // }
                else if (distanceToGhost < MINDISTANCETOBUST) {
                    action = 'MOVE ' + (closestGhost.x + 850) + ' ' + (closestGhost.y + 850);
                } else {

                    action = 'MOVE ' + closestGhost.x + ' ' + closestGhost.y;
                    // closestGhost.chasedBy = this.id;
                    // this.chasedGhost = closestGhost.id;
                }

                if (closestBuster !== null && calculateDistance(this.x, this.y, closestBuster.x, closestBuster.y) < DISTANCETOSTUN && this.turnBeforStun === 0) {
                    action = 'STUN ' + closestBuster.id;
                    this.turnBeforStun = 20;
                }
            }
        }


        if (action.indexOf('STUN') === -1 && this.turnBeforStun > 0) {
            this.turnBeforStun--;
        }

        // if (action === 'MOVE ' + this.x + ' ' + this.y || (action.indexOf('BUST') === -1 && action === this.lastAction)){
        //     printErr ('Retrying because: ' + action);
        //     this.chasedGhost = null;
        //     if(closestGhost !== null && typeof closestGhost !== 'undefined') {
        //         ghostArray.splice(getGhostIndex(closestGhost.id), 1);
        //     }
        //     return this.getAction(ghostArray, opponentBusters);
        // }

        this.lastAction = action;
        return action;
    }
}

class Ghost {
    constructor(id, x, y, busting) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.busting = busting;
        this.chasedBy = null;
        this.visible = true;
    }
}

function calculateDistance(x1, y1, x2, y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
}

var baseX, otherBaseX;
var baseY, otherBaseY;
var otherBase = {x: 0, y: 0};
switch (myTeamId) {
    case 0:
        baseX = 0;
        baseY = 0;
        // otherBaseX = 16000;
        // otherBaseY = 9000;
        otherBase.x = 16000;
        otherBase.y = 9000;
        break;
    default:
        baseX = 16000;
        baseY = 9000;
        // otherBaseX = 0;
        // otherBaseY = 0;
        break;
}
var allGhosts = [];
var myBusters = [];
// game loop
while (true) {

    var opponentBusters = [];

    var entities = parseInt(readline()); // the number of busters and ghosts visible to you

    for (var i = 0; i < entities; i++) {
        var inputs = readline().split(' ');
        var entityId = parseInt(inputs[0]); // buster id or ghost id
        var x = parseInt(inputs[1]);
        var y = parseInt(inputs[2]); // position of this buster / ghost
        var entityType = parseInt(inputs[3]); // the team id if it is a buster, -1 if it is a ghost.
        var state = parseInt(inputs[4]); // For busters: 0=idle, 1=carrying a ghost.
        var value = parseInt(inputs[5]); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.

        if (entityType === myTeamId) {
            var busterIndex = getBusterIndex(entityId);
            if (busterIndex === -1) {
                myBusters.push(new Buster(entityId, x, y, state, value));

            } else {

                myBusters[busterIndex].x = x;
                myBusters[busterIndex].y = y;
                myBusters[busterIndex].state = state;
                myBusters[busterIndex].ghostCarried = value;

            }

        } else if (entityType === -1) {
            var ghostIndex = getGhostIndex(entityId);
            if (ghostIndex === -1) {
                allGhosts.push(new Ghost(entityId, x, y, value));

            } else {

                allGhosts[ghostIndex].x = x;
                allGhosts[ghostIndex].y = y;
                allGhosts[ghostIndex].busting = value;
                allGhosts[ghostIndex].visible = true;
            }


        } else if (entityType !== myTeamId) {
            opponentBusters.push(new Buster(entityId, x, y, state));

        }

    }
    for (var i = 0; i < bustersPerPlayer; i++) {

        // Write an action using print()
        // To debug: printErr('Debug messages...');

        var action = myBusters[i].getAction(allGhosts, opponentBusters);

        print(action); // MOVE x y | BUST id | RELEASE

        allGhosts.forEach(function (ghost, index) {
            ghost.visible = false;
        });

    }

}


function getBusterIndex(busterId) {
    var idx = -1;
    for (var i = 0; i < myBusters.length; i++) {
        if (myBusters[i].id === busterId) {
            return i;
        }
    }

    return idx;
}

function getGhostIndex(ghostId) {
    var idx = -1;
    for (var i = 0; i < allGhosts.length; i++) {
        if (allGhosts[i].id === ghostId) {
            return i;
        }
    }

    return idx;
}

function chooseCorner() {

    if (randCorner > 1) {
        randCorner = 0;
        return otherBase;

    } else {
        return corners[randCorner++];

    }

}