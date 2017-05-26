import React from 'react';
import Link from './Link';

const PrevNext = ({prev, next, prevUrl, nextUrl}) => {
    return (
        <div>
            <hr />
            <ul className="pager">
                {prev &&
                <li className="previous">
                    <Link url={prevUrl} onClick={prev}>
                        &larr; Older
                    </Link>
                </li>
                }
                {next &&
                <li className="next">
                    <Link url={nextUrl} onClick={next}>
                        Newer &rarr;
                    </Link>
                </li>
                }
            </ul>
        </div>
    );
};

export default PrevNext;